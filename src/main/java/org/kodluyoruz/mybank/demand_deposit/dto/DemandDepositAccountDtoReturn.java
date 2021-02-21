package org.kodluyoruz.mybank.demand_deposit.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDtoReturn {
    private String iban;
}
