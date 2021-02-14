package org.kodluyoruz.mybank.debit_card;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/cards/debitcard")
public class DebitCardController {
    private final DebitCardService DebitCardService;

    public DebitCardController(DebitCardService DebitCardService) {
        this.DebitCardService = DebitCardService;
    }

    @PostMapping("/{iban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardDtoReturn create(@Valid @RequestBody DebitCardDto debitCardDto, @PathVariable(name = "iban") String iban) {
        DebitCard debitCard = new DebitCard();
        LocalDate expirationDate=LocalDate.now().plusYears(4);
        debitCard.setPassword(debitCardDto.getPassword());
        debitCard.setExpirationDate(expirationDate);
        return DebitCardService.create(debitCard, iban).toDebitCardDtoReturn();
    }



}