package org.kodluyoruz.mybank.demand_deposit;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDtoReturn {
    private String iban;
}
