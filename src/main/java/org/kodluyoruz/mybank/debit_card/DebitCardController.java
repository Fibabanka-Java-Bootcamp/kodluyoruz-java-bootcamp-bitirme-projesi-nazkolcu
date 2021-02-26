package org.kodluyoruz.mybank.debit_card;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.customer.CustomerRepository;
import org.kodluyoruz.mybank.debit_card.dto.DebitCardDto;
import org.kodluyoruz.mybank.debit_card.dto.DebitCardDtoReturn;
import org.kodluyoruz.mybank.debit_card.dto.DebitCardDtoWithBalance;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoStatement;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.kodluyoruz.mybank.operations.CardOperations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cards/debitcard")
public class DebitCardController extends CardOperations {
    private final DebitCardService debitCardService;


    public DebitCardController(DebitCardService debitCardService) {
        this.debitCardService = debitCardService;

    }

    @PostMapping("/{iban}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardDtoReturn create(@Valid @RequestBody DebitCardDto debitCardDto, @PathVariable(name = "iban") String iban) {
        DebitCard debitCard = new DebitCard();
        LocalDate expirationDate = expirationDateGenerator();
        debitCard.setPassword(debitCardDto.getPassword());
        debitCard.setExpirationDate(expirationDate);
        debitCard.setCvv(cvcGenerator());

        return debitCardService.create(debitCard, iban).toDebitCardDtoReturn();
    }

    @DeleteMapping("/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDebitCard(@PathVariable("cardNumber") Long cardNumber) {
        debitCardService.delete(cardNumber);
    }

    @GetMapping("/listbycustomernumber/{customerNumber}")
    public String getListByCustomerNumber(@PathVariable("customerNumber") Long customerNumber) {
        List<DebitCardDtoWithBalance> list = debitCardService.getDebitCardList(customerNumber);
        if (list.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer has no debit card");
        else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }
    @GetMapping("/cardstatement/{cardNumber}")
    public String getStatement(@PathVariable("cardNumber") Long cardNumber) {
        List<DebitCardTransactionDtoStatement> list = debitCardService.getStatement(cardNumber);
        if (list.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Debit Card has no transaction ");
        }else {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            String json = gson.toJson(list);

            return json;
        }

    }

}