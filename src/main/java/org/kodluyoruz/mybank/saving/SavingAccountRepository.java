package org.kodluyoruz.mybank.saving;


import org.springframework.data.repository.CrudRepository;

public interface SavingAccountRepository extends CrudRepository<SavingAccount, String> {

    SavingAccount findByCustomer_CustomerNumberAndBalance_Currency(Long customerNumber,String currency);
    SavingAccount findByIban(String iban);
}
