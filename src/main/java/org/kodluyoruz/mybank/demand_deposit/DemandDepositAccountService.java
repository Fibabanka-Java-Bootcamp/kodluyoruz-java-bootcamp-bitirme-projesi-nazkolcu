package org.kodluyoruz.mybank.demand_deposit;

import org.kodluyoruz.mybank.account_operations.AccountOperations;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DemandDepositAccountService extends AccountOperations {
    private final CustomerRepository customerRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;

    public DemandDepositAccountService(DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, CustomerRepository customerRepository, DemandDepositAccountRepository demandDepositAccountRepository) {
        this.customerRepository = customerRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
    }

    public DemandDepositAccount update(DemandDepositAccount account) {
        return demandDepositAccountRepository.save(account);
    }

    public DemandDepositAccount create(DemandDepositAccount demandDepositAccount, Long customerNumber, String currency) {

        Customer customer1 = customerRepository.findByCustomerNumber(customerNumber);
        if (customer1 != null) {

            DemandDepositAccount demandDepositAccount1 = demandDepositAccountRepository.findByCustomer_CustomerNumberAndBalance_Currency(customerNumber, currency);

            if (demandDepositAccount1 == null) {


                Customer customer = customerRepository.findByCustomernumberContainingIgnoreCase(customerNumber);

                List accounts = (List) customer.getSavingAccounts();
                demandDepositAccount.setCustomer(customer);

                String iban = ibanGenerator(accounts.size(), currency, customerNumber);

                demandDepositAccount.setIban(iban);

                return demandDepositAccountRepository.save(demandDepositAccount);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account found with this customerNumber : " + customerNumber + " and this currency : " + currency);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found with this customerNumber : " + customerNumber);

    }


    public void delete(String iban) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        if (demandDepositAccount != null) {
            double balance = demandDepositAccount.getBalance().getAmount();
            if (balance == 0) {
                demandDepositAccountRepository.delete(demandDepositAccount);
                demandDepositAccountBalanceRepository.delete(demandDepositAccount.getBalance());
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account has money with this iban : " + iban);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this iban : " + iban);

    }

    public DemandDepositAccountBalance getBalance(String iban) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        if (demandDepositAccount != null) {
            DemandDepositAccountBalance balance = demandDepositAccount.getBalance();
            return balance;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demand Deposit Account not found with customer number :" + iban);
    }
}

