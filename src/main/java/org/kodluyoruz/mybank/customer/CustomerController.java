package org.kodluyoruz.mybank.customer;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.dto.CustomerDtoReturn;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.kodluyoruz.mybank.validations.CustomerValidation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/customer")
public class CustomerController implements CustomerValidation {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final SavingAccountRepository savingAccountRepository;

    public CustomerController(SavingAccountRepository savingAccountRepository, DemandDepositAccountRepository demandDepositAccountRepository, CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.savingAccountRepository = savingAccountRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoReturn create(@Valid @RequestBody CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setName(adjustNameAndSurname(customerDto.getName()));
        customer.setSurname(adjustNameAndSurname(customerDto.getSurname()));
        customer.setBirthDate(customerDto.getBirthDate());
        customer.setTckn(customerDto.getTckn());
        return customerService.create(customer).toCustomerDtoReturn();
    }


    @PutMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Optional<Customer> replaceCustomer(@Valid @RequestBody CustomerDto customerDto, @PathVariable("customerNumber") Long customerNumber) {

        return customerService.get(customerNumber)
                .map(customer -> {
                    customer.setName(adjustNameAndSurname(customerDto.getName()));
                    customer.setSurname(adjustNameAndSurname(customerDto.getSurname()));
                    customer.setBirthDate(customerDto.getBirthDate());
                    customer.setTckn(customerDto.getTckn());
                    return customerService.update(customer);
                });
    }

    @DeleteMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCustomer(@PathVariable("customerNumber") Long customerNumber) {


        Customer customer = customerRepository.findByCustomerNumber(customerNumber);
        List<DemandDepositAccount> list = new ArrayList<>();
        List<SavingAccount> list2 = new ArrayList<>();

        if (customer != null) {
            List<DemandDepositAccount> demandDepositAccountsList2 = demandDepositAccountRepository.findAllByCustomer_CustomerNumber(customerNumber);//findAllByCustomer(customer);

            if (!demandDepositAccountsList2.isEmpty()) {

                for (int i = 0; i < demandDepositAccountsList2.size(); i++) {
                    if (demandDepositAccountsList2.get(i).getBalance().getAmount() > 0) {
                        list.add(demandDepositAccountsList2.get(i));

                    }
                }

            }


            List<SavingAccount> savingAccountList2 = savingAccountRepository.findAllByCustomer(customer);
            if (!savingAccountList2.isEmpty()) {

                for (int i = 0; i < savingAccountList2.size(); i++) {
                    if (savingAccountList2.get(i).getBalance().getAmount() > 0) {
                        list2.add(savingAccountList2.get(i));

                    }
                }

            }

            CreditCard creditCard = customer.getCreditCard();

            if (creditCard != null) {

                if (creditCard.getDebt() != 0) {

                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has debt on credit card with customer number :" + customerNumber);
                }
            }


            if (list2.isEmpty()) {
                if (list.isEmpty()) {

                    customerService.delete(customer);

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has money on demand deposit account(s) with customer number :" + customerNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has money on saving account(s) with customer number :" + customerNumber);


        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer number :" + customerNumber);
    }


}
