package org.kodluyoruz.mybank.demand_deposit;

import org.kodluyoruz.mybank.customer.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount,String> {
    DemandDepositAccount findByCustomer_CustomerNumberAndBalance_Currency(Long customerNumber, String currency);

    DemandDepositAccount findByIban(String iban);

    DemandDepositAccount findByIbanAndBalance_Currency(String iban,String currency);

    List<DemandDepositAccount> findAllByCustomer_CustomerNumber(Long customerNumber);
}
