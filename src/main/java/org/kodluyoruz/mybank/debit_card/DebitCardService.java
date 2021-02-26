package org.kodluyoruz.mybank.debit_card;

import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.debit_card.dto.DebitCardDtoWithBalance;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransaction;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransactionRepository;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoStatement;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountService;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DebitCardService {
    private final DemandDepositAccountService demandDepositAccountService;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private final DebitCardTransactionRepository debitCardTransactionRepository;
    private final CustomerRepository customerRepository;

    public DebitCardService(CustomerRepository customerRepository, DebitCardTransactionRepository debitCardTransactionRepository, DemandDepositAccountService demandDepositAccountService, DebitCardRepository debitCardRepository, DemandDepositAccountRepository demandDepositAccountRepository) {

        this.demandDepositAccountService = demandDepositAccountService;
        this.debitCardRepository = debitCardRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.debitCardTransactionRepository = debitCardTransactionRepository;
        this.customerRepository = customerRepository;
    }

    public DebitCard create(DebitCard debitCard, String iban) {

        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        debitCard.setDemandDepositAccount(demandDepositAccount);

        return debitCardRepository.save(debitCard);
    }


    public void delete(Long cardNumber) {
        DebitCard debitCard = debitCardRepository.findByCardNumber(cardNumber);

        if (debitCard != null) {


            debitCardTransactionRepository.deleteAll(debitCard.getDebitCardTransactions());

            debitCardRepository.delete(debitCard);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card not found with this card number : " + cardNumber);

    }

    public List<DebitCardDtoWithBalance> getDebitCardList(Long customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber);
        if (customer != null) {
            List<DemandDepositAccount> list = customer.getDemandDepositAccounts();

            List<DebitCardDtoWithBalance> list2 = new ArrayList<>();
            List<DebitCard> list3 = new ArrayList<>();

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getDebitCard() != null) {
                        list3.add(list.get(i).getDebitCard());
                        list2.add(list.get(i).getDebitCard().toDebitCardDtoWithBalance());
                    }
                }
                if (list3.isEmpty())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no debit card");

                return list2;
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no demand deposit account");

        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with customer number :" + customerNumber);

    }


    public List<DebitCardTransactionDtoStatement> getStatement(Long cardNumber) {
        DebitCard debitCard = debitCardRepository.findByCardNumber(cardNumber);

        if (debitCard != null) {
            List<DebitCardTransaction> list = debitCard.getDebitCardTransactions();
            List<DebitCardTransactionDtoStatement> list2 = new ArrayList<>();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {

                    list2.add(list.get(i).toDebitCardTransactionDtoStatement());
                }

                return list2;
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card has no transaction");

        } else {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Card not found with this card number: " + cardNumber);
        }
    }
}