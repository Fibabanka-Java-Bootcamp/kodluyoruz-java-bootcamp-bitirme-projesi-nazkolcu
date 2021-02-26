package org.kodluyoruz.mybank.demand_deposit;

import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.debit_card.DebitCardRepository;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransactionRepository;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.kodluyoruz.mybank.operations.AccountOperations;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransaction;
import org.kodluyoruz.mybank.saving_transaction.dto.SavingAccountTransactionDtoStatement;
import org.kodluyoruz.mybank.validations.AccountValidation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DemandDepositAccountService extends AccountOperations implements AccountValidation {
    private final CustomerRepository customerRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final DebitCardRepository debitCardRepository;
    private final DebitCardTransactionRepository debitCardTransactionRepository;

    public DemandDepositAccountService(DebitCardTransactionRepository debitCardTransactionRepository, DebitCardRepository debitCardRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, CustomerRepository customerRepository, DemandDepositAccountRepository demandDepositAccountRepository) {
        this.customerRepository = customerRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.debitCardTransactionRepository = debitCardTransactionRepository;
        this.debitCardRepository = debitCardRepository;
    }


    public DemandDepositAccount create(DemandDepositAccount demandDepositAccount, Long customerNumber, String currency) {

        Customer customer = customerRepository.findByCustomerNumber(customerNumber);
        if (customer != null) {

            DemandDepositAccount demandDepositAccount1 = demandDepositAccountRepository.findByCustomer_CustomerNumberAndBalance_Currency(customerNumber, currency);

            if (demandDepositAccount1 == null) {
                if (checkCurrency(currency)) {

                    List accounts = (List) customer.getSavingAccounts();
                    demandDepositAccount.setCustomer(customer);

                    String iban = ibanGenerator(accounts.size(), currency, customerNumber);

                    demandDepositAccount.setIban(iban);

                    return demandDepositAccountRepository.save(demandDepositAccount);
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency. Please select TRY/EUR/USD : " + currency);

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
                demandDepositAccountTransactionRepository.deleteAll(demandDepositAccount.getTransactions());
                demandDepositAccountBalanceRepository.delete(demandDepositAccount.getBalance());
                demandDepositAccountRepository.delete(demandDepositAccount);
                debitCardTransactionRepository.deleteAll(demandDepositAccount.getDebitCard().getDebitCardTransactions());
                debitCardRepository.delete(demandDepositAccount.getDebitCard());
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account has money with this IBAN : " + iban + " Please transfer the money to another account.");

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account not found with this IBAN : " + iban);

    }

    public DemandDepositAccountBalance getBalance(String iban) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        if (demandDepositAccount != null) {
            DemandDepositAccountBalance balance = demandDepositAccount.getBalance();
            return balance;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demand Deposit Account not found with IBAN :" + iban);
    }

    public List<DemandDepositAccountDtoWithBalance> getDemandDepositAccountList(Long customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber);
        if (customer != null) {
            List<DemandDepositAccount> list = customer.getDemandDepositAccounts();
            List<DemandDepositAccountDtoWithBalance> list2 = new ArrayList<>();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {

                    list2.add(list.get(i).toDemandDepositAccountDtoWithBalance());

                }
                //    list.stream().map(DemandDepositAccount::toDemandDepositAccountDtoWithBalance);

                return list2;
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no demand deposit account");

        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer number :" + customerNumber);

    }

    public List<DemandDepositAccountTransactionDtoStatement> getStatement(String iban) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        if (demandDepositAccount != null) {
                List<DemandDepositAccountTransaction> list = demandDepositAccount.getTransactions();
            List<DemandDepositAccountTransactionDtoStatement> list2 = new ArrayList<>();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {

                    list2.add(list.get(i).toDemandDepositAccountTransactionDtoStatement());
                }

                return list2;
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demand Deposit Account has no transaction");

        } else {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demand Deposit Account not found with IBAN :" + iban);
        }
    }

}

