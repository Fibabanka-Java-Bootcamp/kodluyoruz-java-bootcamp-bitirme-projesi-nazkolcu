package org.kodluyoruz.mybank.credit_card_transaction;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreditCardTransactionService extends TransactionOperations {
    private final CreditCardTransactionRepository creditCardTransactionRepository;
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;
    private final DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository;
    private final CreditCardRepository creditCardRepository;

    public CreditCardTransactionService(CreditCardRepository creditCardRepository, DemandDepositAccountTransactionRepository demandDepositAccountTransactionRepository, DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository, CreditCardTransactionRepository creditCardTransactionRepository) {
        this.creditCardTransactionRepository = creditCardTransactionRepository;
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
        this.demandDepositAccountTransactionRepository = demandDepositAccountTransactionRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCardTransaction createS(CreditCard fromCreditCard, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance toBalance, Double fromTotal, Double toTotal) {
      LocalDateTime now = LocalDateTime.now();

        CreditCardTransaction transaction = createCreditCardTransaction(fromCreditCard, now, fromTotal, toDemandDepositAccount.getIban(), "outflow");

        DemandDepositAccountTransaction transaction2 = createDemandDepositAccountTransaction(toDemandDepositAccount, now, toTotal, String.valueOf(fromCreditCard.getCardNumber()), "inflow");

        fromCreditCard.setDebt(calculateInflow(fromCreditCard.getDebt(), fromTotal));
        toBalance.setAmount(calculateInflow(toBalance.getAmount(), toTotal));

        demandDepositAccountBalanceRepository.save(toBalance);
        creditCardRepository.save(fromCreditCard);

        transaction = creditCardTransactionRepository.save(transaction);

        demandDepositAccountTransactionRepository.save(transaction2);

        return transaction;

    }


    public CreditCardTransaction createSAnotherBank(CreditCard creditCard, String toIban, Double total) {

        LocalDateTime now = LocalDateTime.now();
        CreditCardTransaction creditCardTransaction = createCreditCardTransaction(creditCard,now,total,toIban,"outflow");

        creditCard.setDebt(calculateInflow(creditCard.getDebt(), total));

        creditCardRepository.save(creditCard);

        creditCardTransaction = creditCardTransactionRepository.save(creditCardTransaction);

        return creditCardTransaction;
    }


    public CreditCardTransaction create(CreditCard creditCard, Double total) {
        LocalDateTime now = LocalDateTime.now();
        CreditCardTransaction transaction = createCreditCardTransaction(creditCard, now, total, String.valueOf(creditCard.getCardNumber()), "inflow");

        creditCard.setDebt(calculateOutflow(creditCard.getDebt(), total));

        creditCardRepository.save(creditCard);

        return creditCardTransactionRepository.save(transaction);

    }

}
