package org.kodluyoruz.mybank.demand_deposit_transaction;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountTransactionDtoReturn {
    private LocalDateTime dateTime;
    private double total;

}
