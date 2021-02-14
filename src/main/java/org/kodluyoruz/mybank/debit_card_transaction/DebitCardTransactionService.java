package org.kodluyoruz.mybank.debit_card_transaction;

import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DebitCardTransactionService {
    private final DebitCardTransactionRepository debitCardTransactionRepository;

    private DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;

    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;

    public DebitCardTransactionService(DebitCardTransactionRepository debitCardTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository) {
        this.debitCardTransactionRepository = debitCardTransactionRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
    }

    public DebitCardTransaction createDDA(DebitCard debitCard, DemandDepositAccount demandDepositAccount, DemandDepositAccountBalance balance, Double total) {
        DebitCardTransaction transaction = new DebitCardTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction.setDebitCard(debitCard);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        transaction.setToIban(demandDepositAccount.getIban());


        double amount = balance.getAmount();


        amount = amount + total;


        balance.setAmount(amount);


        demandDepositAccountBalanceRepository.save(balance);


        return debitCardTransactionRepository.save(transaction);

    }


    public DebitCardTransaction createSAnotherBank(DebitCard fromDebitCard, DemandDepositAccount fromDemandDepositAccount, String toIban, DemandDepositAccountBalance fromBalance, Double total) {
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        LocalDateTime now = LocalDateTime.now();


        DebitCardTransaction debitCardTransaction = new DebitCardTransaction();
        debitCardTransaction.setDebitCard(fromDebitCard);
        debitCardTransaction.setDateTime(now);
        debitCardTransaction.setTotal(-total);
        debitCardTransaction.setToIban(toIban);


        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-total);
        transaction1.setToIban(toIban);


        double amount1 = fromBalance.getAmount();


        amount1 = amount1 - total;


        fromBalance.setAmount(amount1);


        demandDepositAccountBalanceRepository.save(fromBalance);
        demandDepositAccountTransactionRepository.save(transaction1);


        return debitCardTransactionRepository.save(debitCardTransaction);


    }

    public DebitCardTransaction createS(DebitCard fromDebitCard, DemandDepositAccount fromDemandDepositAccount, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double total) {
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        DemandDepositAccountTransaction transaction2 = new DemandDepositAccountTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-total);
        transaction1.setToIban(toDemandDepositAccount.getIban());

        transaction2.setDemandDepositAccount(toDemandDepositAccount);
        transaction2.setDateTime(now);
        transaction2.setTotal(total);
        transaction2.setToIban(fromDemandDepositAccount.getIban());


        DebitCardTransaction debitCardTransaction = new DebitCardTransaction();
        debitCardTransaction.setDebitCard(fromDebitCard);
        debitCardTransaction.setDateTime(now);
        debitCardTransaction.setTotal(-total);
        debitCardTransaction.setToIban(toDemandDepositAccount.getIban());


        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - total;
        amount2 = amount2 + total;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        demandDepositAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);

        demandDepositAccountTransactionRepository.save(transaction2);
        demandDepositAccountTransactionRepository.save(transaction1);


        return debitCardTransactionRepository.save(debitCardTransaction);

    }
}
