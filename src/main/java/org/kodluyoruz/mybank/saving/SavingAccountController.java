package org.kodluyoruz.mybank.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kodluyoruz.mybank.saving.dto.SavingAccountDto;
import org.kodluyoruz.mybank.saving.dto.SavingAccountDtoReturn;
import org.kodluyoruz.mybank.saving.dto.SavingAccountDtoWithBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.dto.SavingAccountDtoReturnBalance;
import org.kodluyoruz.mybank.saving_transaction.dto.SavingAccountTransactionDtoStatement;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sun.nio.ch.DefaultAsynchronousChannelProvider;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/account/savingaccount")
public class SavingAccountController {

    private final SavingAccountService savingAccountService;

    public SavingAccountController(SavingAccountService savingAccountService) {
        this.savingAccountService = savingAccountService;
    }

    @PostMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingAccountDtoReturn create(@RequestBody SavingAccountDto savingAccountDto, @PathVariable("customerNumber") Long customerNumber) {
        SavingAccount savingAccount = new SavingAccount();
        SavingAccountBalance balance = new SavingAccountBalance();

        balance.setCurrency(savingAccountDto.getCurrency());

        savingAccount.setBalance(balance);

        return savingAccountService.create(savingAccount, customerNumber, savingAccountDto.getCurrency()).toSavingAccountDtoReturn();
    }


    @DeleteMapping("/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSavingAccount(@PathVariable("iban") String iban) {
        savingAccountService.delete(iban);
    }


    @GetMapping("/balance/{iban}")
    public SavingAccountDtoReturnBalance getBalance(@PathVariable("iban") String iban) {
        return savingAccountService.getBalance(iban).toSavingAccountDtoReturnBalance();
    }

    @GetMapping("/listbycustomernumber/{customerNumber}")
    public String getListByCustomerNumber(@PathVariable("customerNumber") Long customerNumber) {
        List<SavingAccountDtoWithBalance> list = savingAccountService.getSavingAccountListByCustomerNumber(customerNumber);
        if (list.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no saving account");

        else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }


    @GetMapping("/accountstatement/{iban}")
    public String getStatement(@PathVariable("iban") String iban) {
        List<SavingAccountTransactionDtoStatement> list = savingAccountService.getStatement(iban);
        if (list.isEmpty())
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Saving Account has no transaction");
        else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }
}
