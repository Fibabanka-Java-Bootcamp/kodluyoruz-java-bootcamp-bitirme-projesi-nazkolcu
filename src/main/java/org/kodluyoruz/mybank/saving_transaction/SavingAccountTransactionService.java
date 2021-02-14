package org.kodluyoruz.mybank.saving_transaction;

import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SavingAccountTransactionService {
    private final SavingAccountTransactionRepository savingAccountTransactionRepository;
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;
    private final SavingAccountBalanceRepository savingAccountBalanceRepository;


    public SavingAccountTransactionService(SavingAccountTransactionRepository savingAccountTransactionRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, SavingAccountBalanceRepository savingAccountBalanceRepository) {
        this.savingAccountTransactionRepository = savingAccountTransactionRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
        this.savingAccountBalanceRepository = savingAccountBalanceRepository;

    }

    public SavingAccountTransaction createSA(SavingAccount fromSavingAccount, DemandDepositAccount toDemandDepositAccount, SavingAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double total) {
        SavingAccountTransaction transaction1 = new SavingAccountTransaction();
        DemandDepositAccountTransaction transaction2 = new DemandDepositAccountTransaction();


        transaction1.setSavingAccount(fromSavingAccount);
        transaction1.setDateTime(LocalDateTime.now());
        transaction1.setTotal(-total);
        transaction1.setToIban(toDemandDepositAccount.getIban());

        transaction2.setDemandDepositAccount(toDemandDepositAccount);
        transaction2.setDateTime(LocalDateTime.now());
        transaction2.setTotal(total);
        transaction2.setToIban(toDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - total;
        amount2 = amount2 + total;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        demandDepositAccountBalanceRepository.save(toBalance);
        savingAccountBalanceRepository.save(fromBalance);


        demandDepositAccountTransactionRepository.save(transaction2);

        return savingAccountTransactionRepository.save(transaction1);

    }

    public SavingAccountTransaction createCurrencySA(SavingAccount fromSavingAccount, DemandDepositAccount toDemandDepositAccount, SavingAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double fromTotal, Double toTotal) {
        SavingAccountTransaction transaction1 = new SavingAccountTransaction();
        DemandDepositAccountTransaction transaction2 = new DemandDepositAccountTransaction();


        transaction1.setSavingAccount(fromSavingAccount);
        transaction1.setDateTime(LocalDateTime.now());
        transaction1.setTotal(-fromTotal);
        transaction1.setToIban(toDemandDepositAccount.getIban());

        transaction2.setDemandDepositAccount(toDemandDepositAccount);
        transaction2.setDateTime(LocalDateTime.now());
        transaction2.setTotal(toTotal);
        transaction2.setToIban(toDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - fromTotal;
        amount2 = amount2 + toTotal;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);
        demandDepositAccountBalanceRepository.save(toBalance);
        savingAccountBalanceRepository.save(fromBalance);


        demandDepositAccountTransactionRepository.save(transaction2);

        return savingAccountTransactionRepository.save(transaction1);

    }
}
