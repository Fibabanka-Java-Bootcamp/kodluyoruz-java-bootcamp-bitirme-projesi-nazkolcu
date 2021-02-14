package org.kodluyoruz.mybank.demand_deposit_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransactionDtoWithCardNumber;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
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
public class DemandDepositAccountTransactionController {

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
        double total = demandDepositAccountTransactionDto.getTotal();
        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);
        if (toDemandDepositAccount != null) {

            DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIban(fromIban);
            if (fromDemandDepositAccount != null) {

                DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();
                DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
                if (fromBalance.getAmount() >= total) {

                    String fromCurrency = fromBalance.getCurrency();

                    String toCurrency = toBalance.getCurrency();
                    if (toCurrency == fromCurrency) {

                        return demandDepositAccountTransactionService.createDDA(fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, total).toDemandDepositAccountTransactionDtoReturn();

                    } else {

                        RestTemplateRoot root = restTemplate_doviz.getForObject("/latest?symbols=" + toCurrency + "&base=" + fromCurrency, RestTemplateRoot.class);
                        double coefficient = 0;
                        if (toCurrency.equals("USD")) coefficient = root.getRates().USD;
                        else if (toCurrency == "EUR") coefficient = root.getRates().EUR;
                        else if (toCurrency == "TRY") coefficient = root.getRates().TRY;
                        System.out.println(toCurrency);
                        System.out.println("***" + coefficient);

                        double toTotal = total * coefficient;
                        System.out.println("to: " + toTotal + "from:" + total);
                        double fromTotal = total;
                        return demandDepositAccountTransactionService.createCurrencyDDA(fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                    }
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + fromIban);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + toIban);


    }

    @PostMapping("/tosaving/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountTransactionDtoReturn transactToSA(@RequestBody DemandDepositAccountTransactionDto demandDepositAccountTransactionDto, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        String toIban = demandDepositAccountTransactionDto.getToIban();
        double total = demandDepositAccountTransactionDto.getTotal();
        DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIban(fromIban);

        if (fromDemandDepositAccount != null) {

            SavingAccount toSavingAccount = savingAccountRepository.findByIban(toIban);
            if (toSavingAccount != null) {

                SavingAccountBalance toBalance = toSavingAccount.getBalance();
                DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
                if (fromBalance.getAmount() >= total) {

                    String fromCurrency = fromBalance.getCurrency();

                    String toCurrency = toBalance.getCurrency();
                    if (toCurrency == fromCurrency) {

                        return demandDepositAccountTransactionService.createSA(fromDemandDepositAccount, toSavingAccount, fromBalance, toBalance, total).toDemandDepositAccountTransactionDtoReturn();

                    } else {
                        RestTemplateRoot root = restTemplate_doviz.getForObject("/latest?symbols=" + toCurrency + "&base=" + fromCurrency, RestTemplateRoot.class);
                        double coefficient = 0;
                        if (toCurrency == "USD") coefficient = root.getRates().USD;
                        else if (toCurrency == "EUR") coefficient = root.getRates().EUR;
                        else if (toCurrency == "TRY") coefficient = root.getRates().TRY;
                        double toTotal = total * coefficient;
                        double fromTotal = total;
                        return demandDepositAccountTransactionService.createCurrencySA(fromDemandDepositAccount, toSavingAccount, fromBalance, toBalance, fromTotal, toTotal).toDemandDepositAccountTransactionDtoReturn();

                    }
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account not found with this iban : " + toIban);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + fromIban);


    }


    @PostMapping("/debtpaymenttocreditcard/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountTransactionDtoReturn transactToCC(@RequestBody CreditCardTransactionDtoWithCardNumber creditCardTransactionDtoWithCardNumber, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        double debtTotal = creditCardTransactionDtoWithCardNumber.getDebt();
        Long toCardNumber = creditCardTransactionDtoWithCardNumber.getToCardNumber();
        DemandDepositAccount fromDemandDepositAccount = demandDepositAccountRepository.findByIban(fromIban);
        if (fromDemandDepositAccount != null) {
            DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();
            if (fromBalance.getAmount() > debtTotal) {


                CreditCard toCreditCard = creditCardRepository.findByCardNumber(toCardNumber);
                if (toCreditCard != null) {

                    return demandDepositAccountTransactionService.createC(fromDemandDepositAccount, fromBalance, toCreditCard, debtTotal).toDemandDepositAccountTransactionDtoReturn();

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not found with this card number : " + toCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromDemandDepositAccount.getIban());

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + fromIban);

    }

}
