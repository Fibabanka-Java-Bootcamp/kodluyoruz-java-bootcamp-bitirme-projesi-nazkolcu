package org.kodluyoruz.mybank.debit_card_transaction;

import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DebitCardTransactionService extends TransactionOperations {
    private final DebitCardTransactionRepository debitCardTransactionRepository;
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;

    public DebitCardTransactionService(DebitCardTransactionRepository debitCardTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository) {
        this.debitCardTransactionRepository = debitCardTransactionRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
    }

    public DebitCardTransaction createDDA(DebitCard debitCard, DemandDepositAccount demandDepositAccount, DemandDepositAccountBalance balance, Double total) {

        LocalDateTime now = LocalDateTime.now();
        DebitCardTransaction transaction = createDebitCardTransaction(debitCard, now, total, demandDepositAccount.getIban(), "inflow");

        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(demandDepositAccount, now, total, String.valueOf(debitCard.getCardNumber()), "inflow");
        balance.setAmount(calculateInflow(balance.getAmount(), total));
        demandDepositAccountBalanceRepository.save(balance);
        transaction = debitCardTransactionRepository.save(transaction);
        demandDepositAccountTransactionRepository.save(transaction2);

        return transaction;

    }


    public DebitCardTransaction createSAnotherBank(DebitCard fromDebitCard, DemandDepositAccount fromDemandDepositAccount, String toIban, DemandDepositAccountBalance fromBalance, Double total) {
        LocalDateTime now = LocalDateTime.now();

        DebitCardTransaction transaction = createDebitCardTransaction(fromDebitCard,now,total,toIban,"outflow");
        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(fromDemandDepositAccount,now,total,toIban,"outflow");

        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), total));

        demandDepositAccountBalanceRepository.save(fromBalance);
        transaction=debitCardTransactionRepository.save(transaction);
        demandDepositAccountTransactionRepository.save(transaction2);


        return transaction;


    }

    public DebitCardTransaction createS(DebitCard fromDebitCard, DemandDepositAccount fromDemandDepositAccount, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double fromTotal,Double toTotal)  {

        LocalDateTime now = LocalDateTime.now();
        DebitCardTransaction transaction = createDebitCardTransaction(fromDebitCard,now,fromTotal,toDemandDepositAccount.getIban(),"outflow");

        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(fromDemandDepositAccount,now,fromTotal,toDemandDepositAccount.getIban(),"outflow");

        DemandDepositAccountTransaction transaction3 = createDemandDepositAccountTransaction(toDemandDepositAccount,now,toTotal,fromDemandDepositAccount.getIban(),"inflow");


        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), fromTotal));
        toBalance.setAmount(calculateInflow(toBalance.getAmount(), toTotal));

        demandDepositAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);

        transaction=debitCardTransactionRepository.save(transaction);
        demandDepositAccountTransactionRepository.save(transaction2);
        demandDepositAccountTransactionRepository.save(transaction3);


        return transaction;

    }
}
