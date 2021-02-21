package org.kodluyoruz.mybank.credit_card;

import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransactionRepository;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardTransactionRepository creditCardTransactionRepository;

    public CreditCardService(CreditCardTransactionRepository creditCardTransactionRepository, CustomerRepository customerRepository, CreditCardRepository creditCardRepository) {

        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.creditCardTransactionRepository = creditCardTransactionRepository;
    }

    public CreditCard create(CreditCard creditCard, Long customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber);

        if (customer != null) {
            CreditCard creditCard2 = creditCardRepository.findByCustomer_CustomerNumber(customerNumber);
            if (creditCard2 == null) {

                creditCard.setCustomer(customer);

                return creditCardRepository.save(creditCard);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card found with card number :" + customer.getCreditCard().getCardNumber());
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found with customer number :" + customerNumber);

    }

    public Optional<CreditCard> get(Long cardNumber) {
        Optional<CreditCard> creditCard = creditCardRepository.findById(cardNumber);

        if (creditCard.isPresent())
            return creditCard;
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Card not found with id :" + cardNumber);
    }

    public List<CreditCardTransaction> getExtract(Long cardNumber) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber);

        if (creditCard != null) {

            List<CreditCardTransaction> creditCardTransactions = creditCard.getCreditCardTransactions();

            creditCardTransactions.stream().filter((n) -> LocalDateTime.now().getMonth() == n.getDateTime().getMonth() && LocalDateTime.now().getYear() == n.getDateTime().getYear() && n.getFlowType().equals("outflow"));

            return creditCardTransactions;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Card not found with id :" + cardNumber);
    }

    public void delete(Long cardNumber) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber);
        if (creditCard != null) {
            double debt = creditCard.getDebt();
            if (debt == 0) {
                creditCardTransactionRepository.deleteAll(creditCard.getCreditCardTransactions());
                creditCardRepository.delete(creditCard);
            }

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit card not found with this card number : " + cardNumber);

    }


}
