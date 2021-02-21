package org.kodluyoruz.mybank.demand_deposit_balance;

import org.springframework.stereotype.Service;

@Service
public class DemandDepositAccountBalanceService {
    private final DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository;

    public DemandDepositAccountBalanceService(DemandDepositAccountBalanceRepository demandDepositAccountBalanceRepository) {
        this.demandDepositAccountBalanceRepository = demandDepositAccountBalanceRepository;
    }
}
