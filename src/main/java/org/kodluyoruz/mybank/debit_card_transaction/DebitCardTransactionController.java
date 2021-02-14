package org.kodluyoruz.mybank.debit_card_transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.debit_card.DebitCardRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/transaction/debitcard")
public class DebitCardTransactionController {
    private final DebitCardTransactionService debitCardTransactionService;
    private final DebitCardRepository debitCardRepository;
    private final RestTemplate restTemplate_doviz;
    private final DemandDepositAccountRepository demandDepositAccountRepository;

    public DebitCardTransactionController(DemandDepositAccountRepository demandDepositAccountRepository, DebitCardTransactionService debitCardTransactionService, DebitCardRepository debitCardRepository, RestTemplateBuilder restTemplateBuilder) {
        this.debitCardTransactionService = debitCardTransactionService;
        this.debitCardRepository = debitCardRepository;
        this.restTemplate_doviz = restTemplateBuilder.rootUri("https://api.exchangeratesapi.io").build();
        this.demandDepositAccountRepository = demandDepositAccountRepository;
    }

    @PostMapping("/toshoppingiban/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardTransactionDtoReturn transactToS(@RequestBody DebitCardTransactionDto debitCardTransactionDto, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
        String toIban = debitCardTransactionDto.getToIban();
        int password = debitCardTransactionDto.getPassword();
        DebitCard fromDebitCard = debitCardRepository.findByCardNumber(fromCardNumber);
        double total = debitCardTransactionDto.getTotal();
        if (fromDebitCard != null) {
            LocalDate now = LocalDate.now();

            if (now.isBefore(fromDebitCard.getExpirationDate())) {
                DemandDepositAccount fromDemandDepositAccount = fromDebitCard.getDemandDepositAccount();
                DemandDepositAccountBalance fromBalance = fromDemandDepositAccount.getBalance();

                if (password == fromDebitCard.getPassword()) {
                    if (fromBalance.getAmount() > total) {
                        DemandDepositAccount toDemandDepositAccount = demandDepositAccountRepository.findByIbanAndBalance_Currency(toIban, "TRY");

                        if (toDemandDepositAccount != null)
                        {
                            DemandDepositAccountBalance toBalance = toDemandDepositAccount.getBalance();


                            return debitCardTransactionService.createS(fromDebitCard, fromDemandDepositAccount, toDemandDepositAccount, fromBalance, toBalance, total).toDebitCardTransactionDtoReturn();
                        } else
                        {
                            return debitCardTransactionService.createSAnotherBank(fromDebitCard, fromDemandDepositAccount, toIban, fromBalance, total).toDebitCardTransactionDtoReturn();
                        }

                    } else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The balance of this account is insufficient : " + fromDemandDepositAccount.getIban());

                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card password is wrong : " + fromCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card has expired : " + fromCardNumber);


        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card not found with this card number : " + fromCardNumber);

    }


    @PostMapping("/depositcashtoatm/{fromcardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DebitCardTransactionDtoReturn transactToDDA(@RequestBody DebitCardTransactionDtoWithoutIban debitCardTransactionDtoWithoutIban, @PathVariable("fromcardNumber") Long fromCardNumber) throws JsonProcessingException {
        double total = debitCardTransactionDtoWithoutIban.getTotal();
        int password = debitCardTransactionDtoWithoutIban.getPassword();
        DebitCard fromDebitCard = debitCardRepository.findByCardNumber(fromCardNumber);
        if (fromDebitCard != null) {
            LocalDate now = LocalDate.now();

            if (now.isBefore(fromDebitCard.getExpirationDate())) {
                if (password == fromDebitCard.getPassword()) {
                    DemandDepositAccount demandDepositAccount = fromDebitCard.getDemandDepositAccount();

                    DemandDepositAccountBalance balance = demandDepositAccount.getBalance();
                    return debitCardTransactionService.createDDA(fromDebitCard, demandDepositAccount, balance, total).toDebitCardTransactionDtoReturn();
                } else
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card password is wrong : " + fromCardNumber);
            } else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card has expired : " + fromCardNumber);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit Card not found with this card number : " + fromCardNumber);


    }
}