package org.kodluyoruz.mybank.demand_deposit_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoWithCardNumber;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDto;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoReturn;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.rest_template.RestTemplateRoot;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@RequestMapping("/api/transaction/demanddepositaccount")
public class DemandDepositAccountTransactionController extends TransactionOperations {

    private final DemandDepositAccountTransactionService demandDepositAccountTransactionService;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final RestTemplate restTemplate_doviz;
    private final CreditCardRepository creditCardRepository;

    public DemandDepositAccountTransactionController(CreditCardRepository creditCardRepository, DemandDepositAccountTransactionService demandDepositAccountTransactionService, DemandDepositAccountRepository demandDepositAccountRepository, RestTemplateBuilder restTemplateBuilder, SavingAccountRepository savingAccountRepository) {
        this.demandDepositAccountTransactionService = demandDepositAccountTransactionService;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.restTemplate_doviz = restTemplateBuilder.rootUri("https://api.exchangeratesapi.io").build();
        this.savingAccountRepository = savingAccountRepository;
        this.creditCardRepository = creditCardRepository;
    }


    @PostMapping("/todemand/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountTransactionDtoReturn transactToDDA(@RequestBody DemandDepositAccountTransactionDto demandDepositAccountTransactionDto, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        String toIban = demandDepositAccountTransactionDto.getToIban();
        double total = 0.0;

        if (checkMoneyFormat(demandDepositAccountTransactionDto.getTotal())) {
            total = adjustStringToDouble(demandDepositAccountTransactionDto.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + demandDepositAccountTransactionDto.getTotal() + " Format should be like : 1.234,56");
        checkTotalForZero(total);

        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);
        if (toDemandDepositAccount != null) {

            DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIban(fromIban);
            if (fromDemandDepositAccount != null) {
                if (toDemandDepositAccount != fromDemandDepositAccount) {
                    DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();
                    DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
                    if (fromBalance.getAmount() >= total) {

                        String fromCurrency = fromBalance.getCurrency();

                        String toCurrency = toBalance.getCurrency();
                        double fromTotal = total, toTotal = total;
                        if (toCurrency.equals(fromCurrency)) {

                            return demandDepositAccountTransactionService.createDDA(fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                        } else {
                            RestTemplateRoot root = getSpecificCurrency(restTemplate_doviz, fromCurrency, toCurrency);
                           double coefficient = getCurrencyCoefficient(root, toCurrency);

                            coefficient = adjustDoubleDigit(coefficient, 4);
                            toTotal = total * coefficient;
                            toTotal = adjustDoubleDigit(toTotal, 2);
                            return demandDepositAccountTransactionService.createDDA(fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                        }
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can not transfer money to same IBAN  : " + fromIban);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this IBAN : " + fromIban);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this IBAN : " + toIban);


    }

    @PostMapping("/tosaving/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountTransactionDtoReturn transactToSA(@RequestBody DemandDepositAccountTransactionDto demandDepositAccountTransactionDto, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        String toIban = demandDepositAccountTransactionDto.getToIban();
        double total = 0.0;
        if (checkMoneyFormat(demandDepositAccountTransactionDto.getTotal())) {
            total = adjustStringToDouble(demandDepositAccountTransactionDto.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + demandDepositAccountTransactionDto.getTotal() + " Format should be like : 1.234,56");

        checkTotalForZero(total);
        DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIban(fromIban);

        if (fromDemandDepositAccount != null) {

            SavingAccount toSavingAccount = savingAccountRepository.findByIban(toIban);
            if (toSavingAccount != null) {
                DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
                SavingAccountBalance toBalance = toSavingAccount.getBalance();

                if (fromBalance.getAmount() >= total) {

                    String fromCurrency = fromBalance.getCurrency();

                    String toCurrency = toBalance.getCurrency();
                    double fromTotal = total, toTotal = total;

                    if (toCurrency.equals(fromCurrency)) {

                        return demandDepositAccountTransactionService.createSA(fromDemandDepositAccount, toSavingAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                    } else {
                       RestTemplateRoot root = getSpecificCurrency(restTemplate_doviz, fromCurrency, toCurrency);
                        double coefficient = getCurrencyCoefficient(root, toCurrency);
                        coefficient = adjustDoubleDigit(coefficient, 4);
                        toTotal = total * coefficient;
                        toTotal = adjustDoubleDigit(toTotal, 2);
                        return demandDepositAccountTransactionService.createSA(fromDemandDepositAccount, toSavingAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                    }
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account not found with this IBAN : " + toIban);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this IBAN : " + fromIban);

    }

    @PostMapping("/debtpaymenttocreditcard/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountTransactionDtoReturn transactToCC(@RequestBody CreditCardTransactionDtoWithCardNumber creditCardTransactionDtoWithCardNumber, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        double debtTotal = 0.0;

        if (checkMoneyFormat(creditCardTransactionDtoWithCardNumber.getDebt())) {
            debtTotal = adjustStringToDouble(creditCardTransactionDtoWithCardNumber.getDebt());
            debtTotal = adjustDoubleDigit(debtTotal, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + creditCardTransactionDtoWithCardNumber.getDebt() + " Format should be like : 1.234,56");
        checkTotalForZero(debtTotal);
        Long toCardNumber = creditCardTransactionDtoWithCardNumber.getToCardNumber();
        DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIbanAndBalance_Currency(fromIban, "TRY");
        if (fromDemandDepositAccount != null) {

            DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
            if (fromBalance.getAmount() >= debtTotal) {

                CreditCard toCreditCard = creditCardRepository.findByCardNumber(toCardNumber);

                if (toCreditCard != null) {
                    if (fromDemandDepositAccount.getCustomer() == toCreditCard.getCustomer()) {


                        return demandDepositAccountTransactionService.createC(fromDemandDepositAccount, fromBalance, toCreditCard, debtTotal).toDemandDepositAccountTransactionDtoReturn();
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not belong this customer!");
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not found with this card number : " + toCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromDemandDepositAccount.getIban());

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account with TRY currency not found with this IBAN : " + fromIban);


    }

}
