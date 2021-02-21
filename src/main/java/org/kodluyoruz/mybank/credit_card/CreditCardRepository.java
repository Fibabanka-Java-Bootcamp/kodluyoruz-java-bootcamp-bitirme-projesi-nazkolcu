package org.kodluyoruz.mybank.credit_card;

import org.springframework.data.repository.CrudRepository;

public interface CreditCardRepository  extends CrudRepository<CreditCard,Long> {

    CreditCard findByCardNumber(Long cardNumber);

    CreditCard findByCustomer_CustomerNumber(Long customerNumber);
}
