package org.kodluyoruz.mybank.credit_card_transaction;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalanceRepository;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreditCardTransactionService {
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

    //Aynı bankadaki ibana gönder
    public CreditCardTransaction createS(CreditCard fromCreditCard, DemandDepositAccount toDemandDepositAccount, DemandDepositAccountBalance toBalance, Double total) {
        CreditCardTransaction transaction1 = new CreditCardTransaction();
        DemandDepositAccountTransaction transaction2 = new DemandDepositAccountTransaction();
        LocalDateTime now = LocalDateTime.now();

        transaction1.setCreditCard(fromCreditCard);
        transaction1.setDateTime(now);
        transaction1.setTotal(-total);
        transaction1.setToIban(toDemandDepositAccount.getIban());

        transaction2.setDemandDepositAccount(toDemandDepositAccount);
        transaction2.setDateTime(now);
        transaction2.setTotal(total);
        String cardNumber = String.valueOf(fromCreditCard.getCardNumber());
        transaction2.setToIban(cardNumber);

        double debt = fromCreditCard.getDebt();
        debt = debt + total;
        fromCreditCard.setDebt(debt);


        double amount = toBalance.getAmount();

        amount = amount + total;


        toBalance.setAmount(amount);

        demandDepositAccountBalanceRepository.save(toBalance);
        creditCardRepository.save(fromCreditCard);

        demandDepositAccountTransactionRepository.save(transaction2);


        return creditCardTransactionRepository.save(transaction1);

    }


    public CreditCardTransaction createSAnotherBank(CreditCard fromCreditCard, String toIban, Double total) {

        LocalDateTime now = LocalDateTime.now();


        CreditCardTransaction creditCardTransaction = new CreditCardTransaction();
        creditCardTransaction.setCreditCard(fromCreditCard);
        creditCardTransaction.setDateTime(now);
        creditCardTransaction.setTotal(-total);
        creditCardTransaction.setToIban(toIban);


        double debt = fromCreditCard.getDebt();
        debt = debt + total;
        fromCreditCard.setDebt(debt);

        creditCardRepository.save(fromCreditCard);


        return creditCardTransactionRepository.save(creditCardTransaction);


    }



    public CreditCardTransaction create(CreditCard creditCard, Double total) {
        CreditCardTransaction transaction = new CreditCardTransaction();
        LocalDateTime now = LocalDateTime.now();
        transaction.setCreditCard(creditCard);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        String cardNumber = String.valueOf(creditCard.getCardNumber());
        transaction.setToIban(cardNumber);



        double debt = creditCard.getDebt();
        debt = debt - total;
        creditCard.setDebt(debt);

        creditCardRepository.save(creditCard);


        return creditCardTransactionRepository.save(transaction);

    }

}
