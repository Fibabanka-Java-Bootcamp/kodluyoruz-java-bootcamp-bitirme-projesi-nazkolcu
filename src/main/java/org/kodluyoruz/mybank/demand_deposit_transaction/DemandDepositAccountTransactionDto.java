package org.kodluyoruz.mybank.demand_deposit_transaction;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountTransactionDto {

    private String toIban;

    private double total;

}
