package org.kodluyoruz.mybank.demand_deposit_transaction;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransactionRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransaction;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DemandDepositAccountTransactionService extends TransactionOperations {
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;
    private final SavingAccountBalanceRepository savingAccountBalanceRepository;
    private final SavingAccountTransactionRepository savingAccountTransactionRepository;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardTransactionRepository creditCardTransactionRepository;

    public DemandDepositAccountTransactionService(CreditCardTransactionRepository creditCardTransactionRepository, CreditCardRepository creditCardRepository, SavingAccountBalanceRepository savingAccountBalanceRepository, SavingAccountTransactionRepository savingAccountTransactionRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository, DemandDepositAccountRepository demandDepositAccountRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository) {
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
        this.savingAccountTransactionRepository = savingAccountTransactionRepository;
        this.savingAccountBalanceRepository = savingAccountBalanceRepository;
        this.creditCardRepository = creditCardRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;

    }


    public DemandDepositAccountTransaction createDDA(DemandDepositAccount fromDemandDepositAccount, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double fromTotal, Double toTotal) {
        LocalDateTime now = LocalDateTime.now();
        DemandDepositAccountTransaction transaction = createDemandDepositAccountTransaction(fromDemandDepositAccount, now, fromTotal, toDemandDepositAccount.getIban(), "outflow");

        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(toDemandDepositAccount, now, toTotal, fromDemandDepositAccount.getIban(), "inflow");

        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), fromTotal));
        toBalance.setAmount(calculateInflow(toBalance.getAmount(), toTotal));

        demandDepositAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);

        transaction = demandDepositAccountTransactionRepository.save(transaction);
        demandDepositAccountTransactionRepository.save(transaction2);

        return transaction;

    }

    public DemandDepositAccountTransaction createSA(DemandDepositAccount fromDemandDepositAccount, SavingAccount toSavingAccount, DemandDepositAccountBalance fromBalance, SavingAccountBalance toBalance, Double fromTotal, Double toTotal) {
        LocalDateTime now = LocalDateTime.now();

        DemandDepositAccountTransaction transaction = createDemandDepositAccountTransaction(fromDemandDepositAccount, now, fromTotal, toSavingAccount.getIban(), "outflow");


        SavingAccountTransaction transaction2 = createSavingAccountTransaction(toSavingAccount, now, toTotal, fromDemandDepositAccount.getIban(), "inflow");

        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), fromTotal));
        toBalance.setAmount(calculateInflow(toBalance.getAmount(), toTotal));

        savingAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);

        transaction = demandDepositAccountTransactionRepository.save(transaction);
        savingAccountTransactionRepository.save(transaction2);

        return transaction;

    }


    public DemandDepositAccountTransaction createC(DemandDepositAccount fromDemandDepositAccount, DemandDepositAccountBalance fromBalance, CreditCard toCreditCard, Double debtTotal) {

        LocalDateTime now = LocalDateTime.now();
        DemandDepositAccountTransaction transaction = createDemandDepositAccountTransaction(fromDemandDepositAccount, now, debtTotal, String.valueOf(toCreditCard.getCardNumber()), "outflow");

        CreditCardTransaction transaction2 = createCreditCardTransaction(toCreditCard, now, debtTotal, fromDemandDepositAccount.getIban(), "inflow");

        fromBalance.setAmount(calculateOutflow(fromBalance.getAmount(), debtTotal));
        toCreditCard.setDebt(calculateOutflow(toCreditCard.getDebt(), debtTotal));

        demandDepositAccountBalanceRepository.save(fromBalance);
        creditCardRepository.save(toCreditCard);

        transaction = demandDepositAccountTransactionRepository.save(transaction);
        creditCardTransactionRepository.save(transaction2);

        return transaction;

    }

}
