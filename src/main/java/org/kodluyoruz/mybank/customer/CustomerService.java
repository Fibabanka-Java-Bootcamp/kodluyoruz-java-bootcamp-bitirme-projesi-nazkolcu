package org.kodluyoruz.mybank.customer;

import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.debit_card.DebitCardRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private CreditCardRepository creditCardRepository;

    public CustomerService(SavingAccountRepository savingAccountRepository, DemandDepositAccountRepository demandDepositAccountRepository, DebitCardRepository debitCardRepository, CreditCardRepository creditCardRepository, CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.debitCardRepository = debitCardRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.savingAccountRepository = savingAccountRepository;
    }

    public void delete(Long customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber);

        if (customer != null) {

            List<DemandDepositAccount> demandDepositAccountList = customer.getDemandDepositAccounts();

            demandDepositAccountList.stream().filter((n) -> n.getBalance().getAmount() > 0);

            List<SavingAccount> savingAccountList = customer.getSavingAccounts();

            savingAccountList.stream().filter((n) -> n.getBalance().getAmount() > 0);
            if (savingAccountList.isEmpty()) {
                if (demandDepositAccountList.isEmpty()) {

                    if (customer.getCreditCard().getDebt() == 0) {

                        customerRepository.delete(customer);
                        creditCardRepository.delete(customer.getCreditCard());
                        List<DebitCard> debitCards = null;
                        List<DemandDepositAccount> demandDepositAccountList2 = customer.getDemandDepositAccounts();
                        demandDepositAccountList2.stream().filter((n) -> debitCards.add(n.getDebitCard()));

                        debitCardRepository.deleteAll(debitCards);
                        demandDepositAccountRepository.deleteAll(customer.getDemandDepositAccounts());

                        savingAccountRepository.deleteAll(customer.getSavingAccounts());

                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has debt on credit card with customer number :" + customerNumber);

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has money on demand deposit account(s) with customer number :" + customerNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has money on saving account(s) with customer number :" + customerNumber);


        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer number :" + customerNumber);

    }

    public Customer create(Customer customer) {
        Customer customer1 = customerRepository.findByTckn(customer.tckn);
        if (customer1 == null)
            return customerRepository.save(customer);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer found with this tckn : " + customer.getTckn());
    }

    public Customer update(Customer customer) {
        return customerRepository.save(customer);
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
