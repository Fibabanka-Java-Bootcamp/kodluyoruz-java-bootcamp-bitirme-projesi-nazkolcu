package org.kodluyoruz.mybank.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Page<Customer> findAll(Pageable page);

    @Query("select c from Customer c where c.customerNumber = ?1")
    Customer findByCustomernumberContainingIgnoreCase(Long customerNumber);

    Customer findByTckn(String tckn);
    Customer findByCreditCard_CardNumber(Long customerNumber);

    Customer findByCustomerNumber(long customerNumber);

    void deleteByCustomerNumber(Long customerNumber);

    @Modifying
    @Query("delete from Customer t where t.customerNumber = ?1")
    void delete2(Long entityId);

}
