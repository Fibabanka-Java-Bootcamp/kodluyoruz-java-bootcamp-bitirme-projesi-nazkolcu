package org.kodluyoruz.mybank.saving_transaction;

import org.springframework.data.repository.CrudRepository;

public interface SavingAccountTransactionRepository  extends CrudRepository<SavingAccountTransaction,Integer> {
}
