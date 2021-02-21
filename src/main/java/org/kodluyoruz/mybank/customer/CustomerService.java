package org.kodluyoruz.mybank.customer;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card.CreditCardService;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransactionRepository;
import org.kodluyoruz.mybank.debit_card.DebitCardRepository;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransactionRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountService;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.kodluyoruz.mybank.saving.SavingAccountService;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransactionRepository;
import org.kodluyoruz.mybank.validations.CustomerValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService implements CustomerValidation {
    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardTransactionRepository creditCardTransactionRepository;
    private final DebitCardTransactionRepository debitCardTransactionRepository;
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;
    private final SavingAccountTransactionRepository savingAccountTransactionRepository;
    private final SavingAccountBalanceRepository savingAccountBalanceRepository;
    private final SavingAccountService savingAccountService;
    private final DemandDepositAccountService demandDepositAccountService;
    private final CreditCardService creditCardService;

    public CustomerService(CreditCardService creditCardService, DemandDepositAccountService demandDepositAccountService, SavingAccountService savingAccountService, SavingAccountBalanceRepository savingAccountBalanceRepository, SavingAccountTransactionRepository savingAccountTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository, DebitCardTransactionRepository debitCardTransactionRepository, CreditCardTransactionRepository creditCardTransactionRepository, SavingAccountRepository savingAccountRepository, DemandDepositAccountRepository demandDepositAccountRepository, DebitCardRepository debitCardRepository, CreditCardRepository creditCardRepository, CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.debitCardRepository = debitCardRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.savingAccountRepository = savingAccountRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.debitCardTransactionRepository = debitCardTransactionRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
        this.savingAccountTransactionRepository = savingAccountTransactionRepository;
        this.savingAccountBalanceRepository = savingAccountBalanceRepository;
        this.savingAccountService = savingAccountService;
        this.demandDepositAccountService = demandDepositAccountService;
        this.creditCardService = creditCardService;
    }

    public void delete(Customer customer) {

        Long customerNumber = customer.getCustomerNumber();

        customerRepository.delete(customer);


        List<SavingAccount> savingAccountList = savingAccountRepository.findAllByCustomer_CustomerNumber(customerNumber);
        List<DemandDepositAccount> demandDepositAccountList = demandDepositAccountRepository.findAllByCustomer_CustomerNumber(customerNumber);
        CreditCard creditCard = creditCardRepository.findByCustomer_CustomerNumber(customerNumber);


        if (creditCard != null) {
            creditCardService.delete(creditCard.getCardNumber());
        }


        if (!demandDepositAccountList.isEmpty()) {

            for (int i = 0; i < demandDepositAccountList.size(); i++) {
                demandDepositAccountService.delete(demandDepositAccountList.get(i).getIban());
            }
        }


        if (!savingAccountList.isEmpty()) {
            for (int i = 0; i < savingAccountList.size(); i++) {
                savingAccountService.delete(savingAccountList.get(i).getIban());
            }

        }

    }

    public Customer create(Customer customer) {
        Customer customer1 = customerRepository.findByTckn(customer.tckn);
        if (customer1 == null) {
            if (checkCustomerName(customer.getName())) {
                if (checkCustomerSurname(customer.getSurname())) {
                    if (checkCustomerTckn(customer.getTckn())) {
                        return customerRepository.save(customer);
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid TCKN : " + customer.getTckn() + " TCKN must include only numeric characters");
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid surname : " + customer.getSurname() + " Surname must include only alphabetic characters and \".\",\"'\",\"-\"");
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name : " + customer.getName() + " Name  must include only alphabetic characters and \".\",\"'\",\"-\"");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer found with this tckn : " + customer.getTckn());
    }

    public Customer update(Customer customer) {
        if (checkCustomerName(customer.getName())) {
            if (checkCustomerSurname(customer.getSurname())) {

                    return customerRepository.save(customer);
           } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid surname : " + customer.getSurname() + " Surname must include only alphabetic characters and \".\",\"'\",\"-\"");
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name : " + customer.getName() + " Name  must include only alphabetic characters and \".\",\"'\",\"-\"");

    }

    public Page<Customer> list(PageRequest page) {
        return customerRepository.findAll(page);
    }


    public Optional<Customer> get(Long customerNumber) {
        Optional<Customer> customer = customerRepository.findById(customerNumber);

        if (customer.isPresent())
            return customer;
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer number :" + customerNumber);
    }


}
