package org.kodluyoruz.mybank.credit_card_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card.CreditCardRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/transaction/creditcard")
public class CreditCardTransactionController {
    private final CreditCardTransactionService creditCardTransactionService;
    private final CreditCardRepository creditCardRepository;
    private final DemandDepositAccountRepository demandDepositAccountRepository;

    public CreditCardTransactionController(DemandDepositAccountRepository demandDepositAccountRepository, CreditCardTransactionService creditCardTransactionService, CreditCardRepository creditCardRepository) {
        this.creditCardTransactionService = creditCardTransactionService;
        this.creditCardRepository = creditCardRepository;
        this.demandDepositAccountRepository = demandDepositAccountRepository;
    }

    @PostMapping("/toshoppingiban/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardTransactionDtoReturn transactToS(@RequestBody CreditCardTransactionDto creditCardTransactionDto, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
        String toIban = creditCardTransactionDto.getToIban();
        int password = creditCardTransactionDto.getPassword();

        double total = creditCardTransactionDto.getTotal();
        CreditCard fromCreditCard = creditCardRepository.findByCardNumber(fromCardNumber);
        if (fromCreditCard != null) {
            LocalDate now = LocalDate.now();
            if (now.isBefore(fromCreditCard.getExpirationDate())) {
                if (password == fromCreditCard.getPassword()) {

                    double cardLimit = fromCreditCard.getCardLimit();
                    double cardDebt = fromCreditCard.getDebt();

                    double availableCredit = cardLimit - cardDebt;

                    if (availableCredit > 0 && availableCredit >= total) {
                        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIbanAndBalance_Currency(toIban, "TRY");

                        if (toDemandDepositAccount != null) {

                            DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();


                            return creditCardTransactionService.createS(fromCreditCard, toDemandDepositAccount, toBalance, total).toCreditCardTransactionDtoReturn();


                        } else {
                            return creditCardTransactionService.createSAnotherBank(fromCreditCard, toIban, total).toCreditCardTransactionDtoReturn();
                        }


                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this credit card is insufficient : " + fromCreditCard.getCardNumber());
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
        double total = creditCardTransactionDtoWithoutIban.getTotal();
        int password = creditCardTransactionDtoWithoutIban.getPassword();
        CreditCard fromCreditCard = creditCardRepository.findByCardNumber(fromCardNumber);
        if (fromCreditCard != null) {
            LocalDate now = LocalDate.now();
            if (now.isBefore(fromCreditCard.getExpirationDate())) {

                if (password == fromCreditCard.getPassword()) {

                    return creditCardTransactionService.create(fromCreditCard, total).toCreditCardTransactionDtoReturn();

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card password is wrong : " + fromCardNumber);

            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card has expired : " + fromCardNumber);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit Card not found with this card number : " + fromCardNumber);


    }

}
