package org.kodluyoruz.mybank.credit_card;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cards/creditcard")
public class CreditCardController {
    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }


    @PostMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDtoReturn create(@Valid @RequestBody CreditCardDto creditCardDto, @PathVariable(name = "customerNumber") Long customerNumber) {
        CreditCard creditCard = new CreditCard();
        LocalDate expirationDate = LocalDate.now().plusYears(4);
        creditCard.setPassword(creditCardDto.getPassword());
        creditCard.setExpirationDate(expirationDate);
        creditCard.setCardLimit(creditCardDto.getCardLimit());

        return creditCardService.create(creditCard, customerNumber).toCreditCardDtoReturn();
    }


    @GetMapping("/debtinquiry/{cardNumber}")
    public CreditCardDtoReturnDebt getDebt(@PathVariable("cardNumber") Long cardNumber) {
        return creditCardService.get(cardNumber).get().toCreditCardDtoReturnDebt();
   }

    @GetMapping("/extract/{cardNumber}")
    public String getExtract(@PathVariable("cardNumber") Long cardNumber) {
        List creditCardTransactions = creditCardService.getExtract(cardNumber);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String json = gson.toJson(creditCardTransactions);
        return json;
  }


}
