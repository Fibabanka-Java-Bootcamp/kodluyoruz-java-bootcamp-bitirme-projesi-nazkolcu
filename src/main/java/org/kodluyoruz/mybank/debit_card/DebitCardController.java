package org.kodluyoruz.mybank.debit_card;

import org.kodluyoruz.mybank.debit_card.dto.DebitCardDto;
import org.kodluyoruz.mybank.debit_card.dto.DebitCardDtoReturn;
import org.kodluyoruz.mybank.operations.CardOperations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/cards/debitcard")
public class DebitCardController extends CardOperations {
    private final DebitCardService DebitCardService;

    public DebitCardController(DebitCardService DebitCardService) {
        this.DebitCardService = DebitCardService;
    }

    @PostMapping("/{iban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardDtoReturn create(@Valid @RequestBody DebitCardDto debitCardDto, @PathVariable(name = "iban") String iban) {
        DebitCard debitCard = new DebitCard();
        LocalDate expirationDate=expirationDateGenerator();
        debitCard.setPassword(debitCardDto.getPassword());
        debitCard.setExpirationDate(expirationDate);
        debitCard.setCvv(cvcGenerator());

        return DebitCardService.create(debitCard, iban).toDebitCardDtoReturn();
    }



}