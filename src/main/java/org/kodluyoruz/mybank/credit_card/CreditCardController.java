package org.kodluyoruz.mybank.credit_card;

import com.google.gson.*;
import org.hibernate.type.Type;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDto;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDtoReturn;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDtoReturnDebt;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDtoWithDebt;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoExtract;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.operations.CardOperations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cards/creditcard")
public class CreditCardController extends CardOperations {
    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }


    @PostMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDtoReturn create(@Valid @RequestBody CreditCardDto creditCardDto, @PathVariable(name = "customerNumber") Long customerNumber) {
        Double cardLimit = 0.0;
        CreditCard creditCard = new CreditCard();
        LocalDate expirationDate = expirationDateGenerator();
        creditCard.setPassword(creditCardDto.getPassword());
        creditCard.setExpirationDate(expirationDate);
        creditCard.setCvv(cvcGenerator());

        if (checkMoneyFormat(creditCardDto.getCardLimit())) {
            cardLimit = adjustStringToDouble(creditCardDto.getCardLimit());
            cardLimit = adjustDoubleDigit(cardLimit, 2);

        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Money format not correct : " + creditCardDto.getCardLimit() + " Format should be like : 1.234,56");

        if (cardLimit > 0.0)
            creditCard.setCardLimit(cardLimit);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit can not be 0 and negative : " + creditCardDto.getCardLimit());


        return creditCardService.create(creditCard, customerNumber).toCreditCardDtoReturn();
    }


    @GetMapping("/debtinquiry/{cardNumber}")
    public CreditCardDtoReturnDebt getDebt(@PathVariable("cardNumber") Long cardNumber) {

        return creditCardService.get(cardNumber).get().toCreditCardDtoReturnDebt();
    }

    @GetMapping("/extract/{cardNumber}")
    public String getExtract(@PathVariable("cardNumber") Long cardNumber) {
        List<CreditCardTransactionDtoExtract> creditCardTransactions = creditCardService.getExtract(cardNumber);


        if (creditCardTransactions.isEmpty())
            return "There is no spending";
        else {

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(creditCardTransactions);
            return json;
        }
    }

    @DeleteMapping("/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCreditCard(@PathVariable("cardNumber") Long cardNumber) {
        creditCardService.delete(cardNumber);
    }

    @GetMapping("/getbycustomernumber/{customerNumber}")
    public CreditCardDtoWithDebt getCreditCardByCustomerNumber(@PathVariable("customerNumber") Long customerNumber) {
        CreditCardDtoWithDebt creditCardDtoWithDebt = creditCardService.getCreditCard(customerNumber);
        if (creditCardDtoWithDebt != null)
            return creditCardDtoWithDebt;
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no credit card transaction");
    }
}
