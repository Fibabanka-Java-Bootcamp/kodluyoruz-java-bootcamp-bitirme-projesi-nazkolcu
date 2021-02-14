package org.kodluyoruz.mybank.credit_card;

import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CustomerService customerService, CustomerRepository customerRepository, CreditCardRepository creditCardRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard create(CreditCard creditCard, Long customerNumber) {
        Customer customer = customerRepository.findByCreditCard_CardNumber(customerNumber);

        if (customer == null) {

            creditCard.setCustomer(customer);

            return creditCardRepository.save(creditCard);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Card found with card numbar :" + customer.getCreditCard().getCardNumber());
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

            creditCardTransactions.stream().filter((n) -> LocalDateTime.now().getMonth() == n.getDateTime().getMonth() && LocalDateTime.now().getYear() == n.getDateTime().getYear());

            return creditCardTransactions;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Card not found with id :" + cardNumber);
    }
}
