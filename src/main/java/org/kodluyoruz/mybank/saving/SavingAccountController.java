package org.kodluyoruz.mybank.saving;

import org.kodluyoruz.mybank.saving.dto.SavingAccountDto;
import org.kodluyoruz.mybank.saving.dto.SavingAccountDtoReturn;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.dto.SavingAccountDtoReturnBalance;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        SavingAccountBalance balance=new SavingAccountBalance();

        balance.setCurrency(savingAccountDto.getCurrency());

        savingAccount.setBalance(balance);

        return savingAccountService.create(savingAccount,customerNumber,savingAccountDto.getCurrency()).toSavingAccountDtoReturn();
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

}
