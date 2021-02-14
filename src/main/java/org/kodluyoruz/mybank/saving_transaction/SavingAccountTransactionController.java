package org.kodluyoruz.mybank.saving_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping("/api/transaction/savingaccount")
public class SavingAccountTransactionController {

    private final RestTemplate restTemplate_doviz;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final SavingAccountTransactionService savingAccountTransactionService;

    public SavingAccountTransactionController(RestTemplateBuilder restTemplateBuilder, DemandDepositAccountRepository demandDepositAccountRepository, SavingAccountRepository savingAccountRepository, SavingAccountTransactionService savingAccountTransactionService) {
        this.restTemplate_doviz = restTemplateBuilder.rootUri("https://api.exchangeratesapi.io").build();

        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.savingAccountRepository = savingAccountRepository;
        this.savingAccountTransactionService = savingAccountTransactionService;
    }

    @PostMapping("/todemand/{fromIban}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingAccountTransactionDtoReturn transactToDDA(@RequestBody SavingAccountTransactionDto savingAccountTransactionDto, @PathVariable("fromIban") String fromIban) throws JsonProcessingException {
        String toIban = savingAccountTransactionDto.getToIban();
        double total = savingAccountTransactionDto.getTotal();
        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);
        if (toDemandDepositAccount != null) {

            SavingAccount fromSavingAccount = savingAccountRepository.findByIban(fromIban);
            if (fromSavingAccount != null) {

                DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();
                SavingAccountBalance fromBalance = fromSavingAccount.getBalance();
                if (fromBalance.getAmount() >= total) {

                    String fromCurrency = fromBalance.getCurrency();

                    String toCurrency = toBalance.getCurrency();
                    if (toCurrency == fromCurrency) {

                        return savingAccountTransactionService.createSA(fromSavingAccount, toDemandDepositAccount, fromBalance, toBalance, total).toSavingAccountTransactionDtoReturn();

                    } else {
                        RestTemplateRoot root = restTemplate_doviz.getForObject("/latest?symbols=" + toCurrency + "&base=" + fromCurrency, RestTemplateRoot.class);
                        double coefficient = 0;
                        if (toCurrency == "USD") coefficient = root.getRates().USD;
                        else if (toCurrency == "EUR") coefficient = root.getRates().EUR;
                        else if (toCurrency == "TRY") coefficient = root.getRates().TRY;
                        double toTotal = total * coefficient;
                        double fromTotal = total;
                        return savingAccountTransactionService.createCurrencySA(fromSavingAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toSavingAccountTransactionDtoReturn();

                    }
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + fromIban);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + toIban);


    }


}
