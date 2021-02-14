package org.kodluyoruz.mybank.debit_card;

import org.springframework.data.repository.CrudRepository;

public interface DebitCardRepository extends CrudRepository<DebitCard, Long> {

    DebitCard findByCardNumber(Long cardNumber);
}