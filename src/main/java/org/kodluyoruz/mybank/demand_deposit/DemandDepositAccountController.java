package org.kodluyoruz.mybank.demand_deposit;

import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountDtoReturnBalance;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/account/demanddepositaccount")
public class DemandDepositAccountController {
    private final DemandDepositAccountService demandDepositAccountService;

    public DemandDepositAccountController(DemandDepositAccountService demandDepositAccountService) {
        this.demandDepositAccountService = demandDepositAccountService;
    }


    @PostMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDtoReturn create(@RequestBody DemandDepositAccountDto demandDepositAccountDto, @PathVariable("customerNumber") Long customerNumber) {
        DemandDepositAccount demandDepositAccount = new DemandDepositAccount();
        DemandDepositAccountBalance balance = new DemandDepositAccountBalance();

        balance.setCurrency(demandDepositAccountDto.getCurrency());

        demandDepositAccount.setBalance(balance);

        return demandDepositAccountService.create(demandDepositAccount, customerNumber, demandDepositAccountDto.getCurrency()).toDemandDepositAccountDtoReturn();
    }

    @GetMapping("/balance/{iban}")
    public DemandDepositAccountDtoReturnBalance getBalance(@PathVariable("iban") String iban) {
        return demandDepositAccountService.getBalance(iban).toDemandDepositAccountDtoReturnBalance();
    }

    @DeleteMapping("/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDemandDepositAccount(@PathVariable("iban") String iban) {
        demandDepositAccountService.delete(iban);
    }


}
