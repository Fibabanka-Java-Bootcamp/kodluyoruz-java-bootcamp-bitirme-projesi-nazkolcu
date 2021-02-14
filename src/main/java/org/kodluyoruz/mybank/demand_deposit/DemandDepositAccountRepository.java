package org.kodluyoruz.mybank.demand_deposit;

import org.springframework.data.repository.CrudRepository;

public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount,String> {
    DemandDepositAccount findByCustomer_CustomerNumberAndBalance_Currency(Long customerNumber, String currency);

    DemandDepositAccount findByIban(String iban);

    DemandDepositAccount findByIbanAndBalance_Currency(String iban,String currency);
}
