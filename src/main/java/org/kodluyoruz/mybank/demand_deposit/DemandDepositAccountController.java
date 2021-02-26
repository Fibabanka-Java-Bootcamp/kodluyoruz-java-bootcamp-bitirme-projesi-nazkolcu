package org.kodluyoruz.mybank.demand_deposit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoReturn;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.dto.DemandDepositAccountDtoReturnBalance;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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


    @GetMapping("/listbycustomernumber/{customerNumber}")
    public String getListByCustomerNumber(@PathVariable("customerNumber") Long customerNumber) {
        List<DemandDepositAccountDtoWithBalance> list = demandDepositAccountService.getDemandDepositAccountList(customerNumber);
        if (list.isEmpty())
            return "There is no demand deposit account!";
        else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }

    @GetMapping("/accountstatement/{iban}")
    public String getStatement(@PathVariable("iban") String iban) {
        List<DemandDepositAccountTransactionDtoStatement> list = demandDepositAccountService.getStatement(iban);
        if (list.isEmpty()) {
//return "There is no transaction on this IBAN!";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demand Deposit Account has no transaction ");
        }else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }
}
