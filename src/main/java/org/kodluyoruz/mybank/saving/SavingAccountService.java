package org.kodluyoruz.mybank.saving;

import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.operations.AccountOperations;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransactionRepository;
import org.kodluyoruz.mybank.validations.AccountValidation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavingAccountService extends AccountOperations implements AccountValidation {

    private final SavingAccountRepository savingAccountRepository;
    private final CustomerRepository customerRepository;
    private final SavingAccountBalanceRepository savingAccountBalanceRepository;
    private final SavingAccountTransactionRepository savingAccountTransactionRepository;

    public SavingAccountService(SavingAccountTransactionRepository savingAccountTransactionRepository,SavingAccountBalanceRepository savingAccountBalanceRepository,SavingAccountRepository savingAccountRepository, CustomerRepository customerRepository) {
        this.savingAccountRepository = savingAccountRepository;
        this.customerRepository = customerRepository;
        this.savingAccountBalanceRepository=savingAccountBalanceRepository;
        this.savingAccountTransactionRepository=savingAccountTransactionRepository;
    }

    public void delete(String iban) {
        SavingAccount savingAccount = savingAccountRepository.findByIban(iban);

        if (savingAccount != null) {
            double balance = savingAccount.getBalance().getAmount();
            if (balance == 0) {
                savingAccountTransactionRepository.deleteAll(savingAccount.getTransactions());
                savingAccountBalanceRepository.delete(savingAccount.getBalance());
                savingAccountRepository.delete(savingAccount);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account has money with this iban : " + iban+" Please transfer the money to another account.");

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account not found with this iban : " + iban);

    }

    public SavingAccount create(SavingAccount savingAccount, Long customerNumber, String currency) {

        Customer customer = customerRepository.findByCustomerNumber(customerNumber);
        if (customer != null) {

            SavingAccount savingAccount1 = savingAccountRepository.findByCustomer_CustomerNumberAndBalance_Currency(customerNumber, currency);

            if (savingAccount1 == null) {
                if (checkCurrency(currency)) {

                List accounts = (List) customer.getSavingAccounts();
                savingAccount.setCustomer(customer);

                String iban = ibanGenerator(accounts.size(), currency, customerNumber);

                savingAccount.setIban(iban);

                return savingAccountRepository.save(savingAccount);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency. Please select TRY/EUR/USD : " + currency);

        } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saving Account found with this customerNumber : " + customerNumber + " and this currency : " + currency);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found with this customerNumber : " + customerNumber);

    }


    public SavingAccountBalance getBalance(String iban) {
        SavingAccount savingAccount = savingAccountRepository.findByIban(iban);

        if (savingAccount!=null){
           SavingAccountBalance balance =  savingAccount.getBalance();
            return balance;}
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Saving Account not found with customer number :" + iban);
    }


}
