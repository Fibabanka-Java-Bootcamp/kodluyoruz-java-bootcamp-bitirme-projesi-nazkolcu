package org.kodluyoruz.mybank.saving_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.rest_template.RestTemplateRoot;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_transaction.dto.SavingAccountTransactionDto;
import org.kodluyoruz.mybank.saving_transaction.dto.SavingAccountTransactionDtoReturn;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RestController
@RequestMapping("/api/transaction/savingaccount")
public class SavingAccountTransactionController extends TransactionOperations {

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
        double total = 0.0;

        if (checkMoneyFormat(savingAccountTransactionDto.getTotal())) {
            total = adjustStringToDouble(savingAccountTransactionDto.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + savingAccountTransactionDto.getTotal() + " Format should be like : 1.234,56");
        checkTotalForZero(total);

        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);
        if (toDemandDepositAccount != null) {

            SavingAccount fromSavingAccount = savingAccountRepository.findByIban(fromIban);
            if (fromSavingAccount != null) {
                if (fromSavingAccount.getCustomer() == toDemandDepositAccount.getCustomer()) {
                DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();
                SavingAccountBalance fromBalance = fromSavingAccount.getBalance();
                if (fromBalance.getAmount() >= total) {

                    String fromCurrency = fromBalance.getCurrency();

                    String toCurrency = toBalance.getCurrency();
                    double fromTotal = total, toTotal = total;

                    if (toCurrency.equals(fromCurrency)) {

                        return savingAccountTransactionService.createSA(fromSavingAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toSavingAccountTransactionDtoReturn();

                    } else {
                        RestTemplateRoot root = getSpecificCurrency(restTemplate_doviz, fromCurrency, toCurrency);
                        double coefficient = getCurrencyCoefficient(root, toCurrency);
                        coefficient = adjustDoubleDigit(coefficient, 4);
                        toTotal = total * coefficient;
                        toTotal = adjustDoubleDigit(toTotal, 2);
                        return savingAccountTransactionService.createSA(fromSavingAccount, toDemandDepositAccount, fromBalance, toBalance, fromTotal, toTotal).toSavingAccountTransactionDtoReturn();

                    }
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromIban);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer of these accounts are not the same" );

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account not found with this IBAN : " + fromIban);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this IBAN : " + toIban);


    }


}
