package org.kodluyoruz.mybank.saving_balance;

import org.kodluyoruz.mybank.saving.SavingAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class SavingAccountBalanceService {

    private final SavingAccountRepository savingAccountRepository;

    public SavingAccountBalanceService(SavingAccountRepository savingAccountRepository) {
        this.savingAccountRepository = savingAccountRepository;
    }
}
