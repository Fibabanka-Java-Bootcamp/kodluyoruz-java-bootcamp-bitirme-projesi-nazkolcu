package org.kodluyoruz.mybank.demand_deposit;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDto {
    private String currency;
}
