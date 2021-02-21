package org.kodluyoruz.mybank.debit_card;

import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccountService;
import org.springframework.stereotype.Service;

@Service
public class DebitCardService {
    private final DemandDepositAccountService demandDepositAccountService;
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final DebitCardRepository debitCardRepository;




    public DebitCardService(DemandDepositAccountService demandDepositAccountService, DebitCardRepository debitCardRepository,DemandDepositAccountRepository demandDepositAccountRepository) {

        this.demandDepositAccountService = demandDepositAccountService;
        this.debitCardRepository = debitCardRepository;
        this.demandDepositAccountRepository=demandDepositAccountRepository;
    }

    public DebitCard create(DebitCard debitCard, String iban) {

        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findByIban(iban);

        debitCard.setDemandDepositAccount(demandDepositAccount);

        return debitCardRepository.save(debitCard);
    }
}