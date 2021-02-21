package org.kodluyoruz.mybank.saving;


import org.kodluyoruz.mybank.customer.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SavingAccountRepository extends CrudRepository<SavingAccount, String> {

    SavingAccount findByCustomer_CustomerNumberAndBalance_Currency(Long customerNumber,String currency);
    SavingAccount findByIban(String iban);
    List<SavingAccount> findAllByCustomer(Customer customer);

    List<SavingAccount> findAllByCustomer_CustomerNumber(Long customerNumber);
}
