package org.kodluyoruz.mybank.demand_deposit.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDto {
    private String currency;
}
