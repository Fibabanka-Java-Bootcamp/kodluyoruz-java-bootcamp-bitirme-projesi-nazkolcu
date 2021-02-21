package org.kodluyoruz.mybank.demand_deposit_balance.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDtoReturnBalance {
    private double amount;
}
