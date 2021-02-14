package org.kodluyoruz.mybank.demand_deposit_transaction;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransactionRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalanceRepository;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransaction;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DemandDepositAccountTransactionService {
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


    public DemandDepositAccountTransaction createDDA(DemandDepositAccount fromDemandDepositAccount, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double total) {
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

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - total;
        amount2 = amount2 + total;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        demandDepositAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);
        demandDepositAccountTransactionRepository.save(transaction2);
        return demandDepositAccountTransactionRepository.save(transaction1);

    }


    public DemandDepositAccountTransaction createSA(DemandDepositAccount fromDemandDepositAccount, SavingAccount toSavingAccount, DemandDepositAccountBalance fromBalance, SavingAccountBalance toBalance, Double total) {
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        SavingAccountTransaction transaction2 = new SavingAccountTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-total);
        transaction1.setToIban(toSavingAccount.getIban());

        transaction2.setSavingAccount(toSavingAccount);
        transaction2.setDateTime(now);
        transaction2.setTotal(total);
        transaction2.setToIban(fromDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - total;
        amount2 = amount2 + total;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        savingAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);


        savingAccountTransactionRepository.save(transaction2);

        return demandDepositAccountTransactionRepository.save(transaction1);

    }


    public DemandDepositAccountTransaction createCurrencyDDA(DemandDepositAccount fromDemandDepositAccount, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance fromBalance, DemandDepositAccountBalance toBalance, Double fromTotal, Double toTotal) {
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        DemandDepositAccountTransaction transaction2 = new DemandDepositAccountTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-fromTotal);
        transaction1.setToIban(toDemandDepositAccount.getIban());

        transaction2.setDemandDepositAccount(toDemandDepositAccount);
        transaction2.setDateTime(now);
        transaction2.setTotal(toTotal);
        transaction2.setToIban(fromDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - fromTotal;
        amount2 = amount2 + toTotal;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        demandDepositAccountBalanceRepository.save(fromBalance);
        demandDepositAccountBalanceRepository.save(toBalance);


        demandDepositAccountTransactionRepository.save(transaction2);

        return demandDepositAccountTransactionRepository.save(transaction1);

    }

    public DemandDepositAccountTransaction createCurrencySA(DemandDepositAccount fromDemandDepositAccount, SavingAccount toSavingAccount, DemandDepositAccountBalance fromBalance, SavingAccountBalance toBalance, Double fromTotal, Double toTotal) {
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        SavingAccountTransaction transaction2 = new SavingAccountTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-fromTotal);
        transaction1.setToIban(toSavingAccount.getIban());

        transaction2.setSavingAccount(toSavingAccount);
        transaction2.setDateTime(now);
        transaction2.setTotal(toTotal);
        transaction2.setToIban(fromDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double amount2 = toBalance.getAmount();

        amount1 = amount1 - fromTotal;
        amount2 = amount2 + toTotal;

        fromBalance.setAmount(amount1);
        toBalance.setAmount(amount2);

        savingAccountBalanceRepository.save(toBalance);
        demandDepositAccountBalanceRepository.save(fromBalance);

        savingAccountTransactionRepository.save(transaction2);

        return demandDepositAccountTransactionRepository.save(transaction1);

    }

    public DemandDepositAccountTransaction createC(DemandDepositAccount fromDemandDepositAccount, DemandDepositAccountBalance fromBalance, CreditCard toCreditCard, Double debtTotal) {

        // ddabalance , ddatra , cctra , cc güncelle
        LocalDateTime now = LocalDateTime.now();
        DemandDepositAccountTransaction transaction1 = new DemandDepositAccountTransaction();
        CreditCardTransaction transaction2 = new CreditCardTransaction();

        transaction1.setDemandDepositAccount(fromDemandDepositAccount);
        transaction1.setDateTime(now);
        transaction1.setTotal(-debtTotal);
        String cardNumber = String.valueOf(toCreditCard.getCardNumber());
        transaction1.setToIban(cardNumber);

        transaction2.setCreditCard(toCreditCard);
        transaction2.setDateTime(now);
        transaction2.setTotal(debtTotal);
        transaction2.setToIban(fromDemandDepositAccount.getIban());

        double amount1 = fromBalance.getAmount();
        double cardDebt = toCreditCard.getDebt();

        amount1 = amount1 - debtTotal;
        cardDebt = cardDebt - debtTotal;

        fromBalance.setAmount(amount1);
        toCreditCard.setDebt(cardDebt);


        demandDepositAccountBalanceRepository.save(fromBalance);
        creditCardRepository.save(toCreditCard);
        creditCardTransactionRepository.save(transaction2);


        return demandDepositAccountTransactionRepository.save(transaction1);

    }

}
