package org.kodluyoruz.mybank.debit_card_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.debit_card.DebitCardRepository;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDto;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoReturn;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoWithoutIban;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.rest_template.RestTemplateRoot;
import org.kodluyoruz.mybank.validations.AccountValidation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/transaction/debitcard")
public class DebitCardTransactionController extends TransactionOperations implements AccountValidation {
    private final DebitCardTransactionService debitCardTransactionService;
    private final DebitCardRepository debitCardRepository;
    private final RestTemplate restTemplate_doviz;
    private final DemandDepositAccountRepository demandDepositAccountRepository;

    public DebitCardTransactionController(DemandDepositAccountRepository demandDepositAccountRepository, DebitCardTransactionService debitCardTransactionService, DebitCardRepository debitCardRepository, RestTemplateBuilder restTemplateBuilder) {
        this.debitCardTransactionService = debitCardTransactionService;
        this.debitCardRepository = debitCardRepository;
        this.restTemplate_doviz = restTemplateBuilder.rootUri("https://api.exchangeratesapi.io").build();
        this.demandDepositAccountRepository = demandDepositAccountRepository;
    }

    @PostMapping("/shopping/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardTransactionDtoReturn transactToS(@RequestBody DebitCardTransactionDto debitCardTransactionDto, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
        String toIban = debitCardTransactionDto.getToIban();
        int password = debitCardTransactionDto.getPassword();
        int cvc = debitCardTransactionDto.getCvc();
        DebitCard fromDebitCard = debitCardRepository.findByCardNumber(fromCardNumber);

        double total = 0.0;

        if (checkMoneyFormat(debitCardTransactionDto.getTotal())) {
            total = adjustStringToDouble(debitCardTransactionDto.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + debitCardTransactionDto.getTotal() + " Format should be like : 1.234,56");
        checkTotalForZero(total);

        if (fromDebitCard != null) {
            LocalDate now = LocalDate.now();

            if (now.isBefore(fromDebitCard.getExpirationDate())) {
                DemandDepositAccount fromDemandDepositAccount = fromDebitCard.getDemandDepositAccount();
                DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();

                if (password == fromDebitCard.getPassword()) {
                    if (cvc == fromDebitCard.getCvv()) {
                        if (fromBalance.getAmount() >= total) {
                            if (checkIban(toIban)) {
                                DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);

                                if (toDemandDepositAccount != null) {

                                    if (toDemandDepositAccount.getCustomer() != fromDebitCard.getDemandDepositAccount().getCustomer()) {
                                        DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();
                                        String toCurrency = toBalance.getCurrency();
                                        double fromTotal = total, toTotal = total;
                                        String fromCurrency = fromBalance.getCurrency();

                                        if (toCurrency.equals(fromCurrency)) {

                                            return debitCardTransactionService.createS(fromDebitCard, fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toDebitCardTransactionDtoReturn();
                                        } else {

                                            RestTemplateRoot root = getSpecificCurrency(restTemplate_doviz, fromCurrency, toCurrency);//  RestTemplateRoot root = restTemplate_doviz.getForObject("/latest?symbols=" + toCurrency + "&base=" + fromCurrency, RestTemplateRoot.class);
                                            double coefficient = getCurrencyCoefficient(root, toCurrency);
                                            coefficient = adjustDoubleDigit(coefficient, 4);
                                            toTotal = total * coefficient;
                                            toTotal = adjustDoubleDigit(toTotal, 2);
                                            return debitCardTransactionService.createS(fromDebitCard, fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toDebitCardTransactionDtoReturn();

                                        }

                                    } else
                                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can not use this IBAN : " + toIban);
                                } else {
                                    return debitCardTransactionService.createSAnotherBank(fromDebitCard, fromDemandDepositAccount, toIban, fromBalance, total).toDebitCardTransactionDtoReturn();
                                }

                            } else
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IBAN format not correct : " + toIban);
                        } else
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromDemandDepositAccount.getIban());
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card cvc is wrong : " + fromCardNumber);

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card password is wrong : " + fromCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card has expired : " + fromCardNumber);


        } else
            throw new

                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card not found with this card number : " + fromCardNumber);

    }


    @PostMapping("/depositcashtoatm/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardTransactionDtoReturn transactToDDA(@RequestBody DebitCardTransactionDtoWithoutIban debitCardTransactionDtoWithoutIban, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {

        int password = debitCardTransactionDtoWithoutIban.getPassword();
        double total = 0.0;


        if (checkMoneyFormat(debitCardTransactionDtoWithoutIban.getTotal())) {
            total = adjustStringToDouble(debitCardTransactionDtoWithoutIban.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + debitCardTransactionDtoWithoutIban.getTotal() + " Format should be like : 1.234,56");


        checkTotalForZero(total);
        DebitCard fromDebitCard = debitCardRepository.findByCardNumber(fromCardNumber);
        if (fromDebitCard != null) {
            LocalDate now = LocalDate.now();

            if (now.isBefore(fromDebitCard.getExpirationDate())) {
                if (password == fromDebitCard.getPassword()) {
                    DemandDepositAccount demandDepositAccount = fromDebitCard.getDemandDepositAccount();

                    DemandDepositAccountBalance balance = demandDepositAccount.getBalance();
                    return debitCardTransactionService.createDDA(fromDebitCard, demandDepositAccount, balance, total).toDebitCardTransactionDtoReturn();
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card password is wrong : " + fromCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card has expired : " + fromCardNumber);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card not found with this card number : " + fromCardNumber);


    }
}