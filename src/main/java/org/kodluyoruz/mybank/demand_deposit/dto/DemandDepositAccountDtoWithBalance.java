package org.kodluyoruz.mybank.demand_deposit.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountDtoWithBalance {
    @Expose
    private String iban;
    @Expose
    private double amount ;
    @Expose
    private String currency;
}
