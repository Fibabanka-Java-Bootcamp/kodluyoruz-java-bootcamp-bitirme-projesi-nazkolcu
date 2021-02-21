package org.kodluyoruz.mybank.saving_transaction;

import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SavingAccountTransactionService extends TransactionOperations {
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

    public SavingAccountTransaction createSA(SavingAccount fromSavingAccount, DemandDepositAccount toDemandDepositAccount, SavingAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double fromTotal, Double toTotal) {
        LocalDateTime now = LocalDateTime.now();

        SavingAccountTransaction transaction = createSavingAccountTransaction(fromSavingAccount, now, fromTotal, toDemandDepositAccount.getIban(), "outflow");

        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(toDemandDepositAccount, now, toTotal, toDemandDepositAccount.getIban(), "inflow");

        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), fromTotal));
        toBalance.setAmount(calculateInflow(toBalance.getAmount(), toTotal));

        demandDepositAccountBalanceRepository.save(toBalance);
        savingAccountBalanceRepository.save(fromBalance);

        transaction = savingAccountTransactionRepository.save(transaction);
        demandDepositAccountTransactionRepository.save(transaction2);

        return transaction;

    }
}
