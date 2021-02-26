package org.kodluyoruz.mybank.credit_card_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDto;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoReturn;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoWithoutIban;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.operations.TransactionOperations;
import org.kodluyoruz.mybank.rest_template.RestTemplateRoot;
import org.kodluyoruz.mybank.validations.AccountValidation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/transaction/creditcard")
public class CreditCardTransactionController extends TransactionOperations implements AccountValidation {
    private final CreditCardTransactionService creditCardTransactionService;
    private final CreditCardRepository creditCardRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final RestTemplate restTemplate_doviz;


    public CreditCardTransactionController(DemandDepositAccountRepository demandDepositAccountRepository, RestTemplateBuilder restTemplateBuilder, CreditCardTransactionService creditCardTransactionService, CreditCardRepository creditCardRepository) {
        this.creditCardTransactionService = creditCardTransactionService;
        this.creditCardRepository = creditCardRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.restTemplate_doviz = restTemplateBuilder.rootUri("https://api.exchangeratesapi.io").build();
    }

    @PostMapping("/shopping/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardTransactionDtoReturn transactToS(@RequestBody CreditCardTransactionDto creditCardTransactionDto, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
        String toIban = creditCardTransactionDto.getToIban();
        int password = creditCardTransactionDto.getPassword();
        int cvv = creditCardTransactionDto.getCvv();
        double total = 0.0;


        if (checkMoneyFormat(creditCardTransactionDto.getTotal())) {
            total = adjustStringToDouble(creditCardTransactionDto.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + creditCardTransactionDto.getTotal() + " Format should be like : 1.234,56");

        checkTotalForZero(total);

        CreditCard fromCreditCard = creditCardRepository.findByCardNumber(fromCardNumber);
        if (fromCreditCard != null) {
            LocalDate now = LocalDate.now();
            if (now.isBefore(fromCreditCard.getExpirationDate())) {
                if (password == fromCreditCard.getPassword()) {
                    if (cvv == fromCreditCard.getCvv()) {
                        double cardLimit = fromCreditCard.getCardLimit();
                        double cardDebt = fromCreditCard.getDebt();
                        double availableCredit = cardLimit - cardDebt;

                        if (availableCredit > 0 && availableCredit >= total) {
                            if (checkIban(toIban)) {

                                DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIban(toIban);

                                if (toDemandDepositAccount != null) {
                                    if (toDemandDepositAccount.getCustomer() != fromCreditCard.getCustomer()) {

                                        DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();

                                        String toCurrency = toBalance.getCurrency();
                                        double fromTotal = total, toTotal = total;

                                        if (toCurrency.equals("TRY")) {

                                            return creditCardTransactionService.createS(fromCreditCard, toDemandDepositAccount, toBalance, fromTotal, toTotal).toCreditCardTransactionDtoReturn();

                                        } else {
                                           RestTemplateRoot root = getSpecificCurrency(restTemplate_doviz, "TRY", toCurrency);
                                            double coefficient = getCurrencyCoefficient(root, toCurrency);
                                            coefficient = adjustDoubleDigit(coefficient, 4);
                                            toTotal = total * coefficient;
                                            toTotal = adjustDoubleDigit(toTotal, 2);
                                            return creditCardTransactionService.createS(fromCreditCard, toDemandDepositAccount, toBalance, fromTotal, toTotal).toCreditCardTransactionDtoReturn();
                                        }
                                    } else
                                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can not use this IBAN : " + toIban);


                                } else {
                                    return creditCardTransactionService.createSAnotherBank(fromCreditCard, toIban, total).toCreditCardTransactionDtoReturn();
                                }
                            } else
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IBAN format not correct : " + toIban);
                        } else
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this credit card is insufficient : " + fromCreditCard.getCardNumber());
                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card cvv is wrong : " + fromCardNumber);

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card password is wrong : " + fromCardNumber);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card has expired : " + fromCardNumber);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not found with this card number : " + fromCardNumber);

    }


    @PostMapping("/debtpaymenttoatm/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardTransactionDtoReturn transactToDebt(@RequestBody CreditCardTransactionDtoWithoutIban creditCardTransactionDtoWithoutIban, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
      int password = creditCardTransactionDtoWithoutIban.getPassword();
        double total = 0.0;


        if (checkMoneyFormat(creditCardTransactionDtoWithoutIban.getTotal())) {
            total = adjustStringToDouble(creditCardTransactionDtoWithoutIban.getTotal());
            total = adjustDoubleDigit(total, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + creditCardTransactionDtoWithoutIban.getTotal() + " Format should be like : 1.234,56");

        checkTotalForZero(total);

        CreditCard fromCreditCard = creditCardRepository.findByCardNumber(fromCardNumber);
        if (fromCreditCard != null) {
            LocalDate now = LocalDate.now();
            if (now.isBefore(fromCreditCard.getExpirationDate())) {

                if (password == fromCreditCard.getPassword()) {

                    if (fromCreditCard.getDebt() > 0)
                        return creditCardTransactionService.create(fromCreditCard, total).toCreditCardTransactionDtoReturn();
                    else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card has no debt : " + fromCardNumber);

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card password is wrong : " + fromCardNumber);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card has expired : " + fromCardNumber);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not found with this card number : " + fromCardNumber);


    }

}
