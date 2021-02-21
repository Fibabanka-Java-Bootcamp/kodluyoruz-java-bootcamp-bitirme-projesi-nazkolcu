package org.kodluyoruz.mybank.operations;

import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransaction;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.rest_template.RestTemplateRoot;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransaction;
import org.kodluyoruz.mybank.validations.DoubleValidation;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public class TransactionOperations implements DoubleOperation, DoubleValidation {

    public double getCurrencyCoefficient(RestTemplateRoot root, String toCurrency) {
        double coefficient = 0;
        if (toCurrency.equals("USD")) coefficient = root.getRates().USD;
        else if (toCurrency.equals("EUR")) coefficient = root.getRates().EUR;
        else if (toCurrency.equals("TRY")) coefficient = root.getRates().TRY;
        return coefficient;
    }

    public RestTemplateRoot getSpecificCurrency(RestTemplate restTemplate_doviz, String fromCurrency, String toCurrency) {

        RestTemplateRoot root = restTemplate_doviz.getForObject("/latest?symbols=" + toCurrency + "&base=" + fromCurrency, RestTemplateRoot.class);
        return root;
    }

    public void checkTotalForZero(double total) {
        if (total > 0.0) {
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total can not be 0 and negative");

    }

    public SavingAccountTransaction createSavingAccountTransaction(SavingAccount SavingAccount, LocalDateTime now, Double total, String toIban, String flowType) {
        SavingAccountTransaction transaction = new SavingAccountTransaction();

        if (flowType.equals("inflow")) transaction.setFlowType("inflow");
        else {
            transaction.setFlowType("outflow");
            total = total * -1;
        }

        transaction.setSavingAccount(SavingAccount);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        transaction.setToIban(toIban);

        return transaction;
    }

    public DemandDepositAccountTransaction createDemandDepositAccountTransaction(DemandDepositAccount demandDepositAccount, LocalDateTime now, Double total, String toIban, String flowType) {
        DemandDepositAccountTransaction transaction = new DemandDepositAccountTransaction();
        if (flowType.equals("inflow")) transaction.setFlowType("inflow");
        else {
            transaction.setFlowType("outflow");
            total = total * -1;
        }
        transaction.setDemandDepositAccount(demandDepositAccount);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        transaction.setToIban(toIban);
        return transaction;
    }

    public DebitCardTransaction createDebitCardTransaction(DebitCard debitCard, LocalDateTime now, Double total, String toIban, String flowType) {
        DebitCardTransaction transaction = new DebitCardTransaction();
        if (flowType.equals("inflow")) transaction.setFlowType("inflow");
        else {
            transaction.setFlowType("outflow");
            total = total * -1;
        }
        transaction.setDebitCard(debitCard);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        transaction.setToIban(toIban);
        return transaction;
    }

    public CreditCardTransaction createCreditCardTransaction(CreditCard CreditCard, LocalDateTime now, Double total, String toIban, String flowType) {
        CreditCardTransaction transaction = new CreditCardTransaction();

        if (flowType.equals("inflow")) transaction.setFlowType("inflow");
        else {
            transaction.setFlowType("outflow");
            total = total * -1;
        }
        transaction.setCreditCard(CreditCard);
        transaction.setDateTime(now);
        transaction.setTotal(total);
        transaction.setToIban(toIban);
        return transaction;
    }

    public double calculateOutflow(Double amount, Double total) {
        return amount - total;
    }

    public double calculateInflow(Double amount, Double total) {
        return amount + total;
    }


}
