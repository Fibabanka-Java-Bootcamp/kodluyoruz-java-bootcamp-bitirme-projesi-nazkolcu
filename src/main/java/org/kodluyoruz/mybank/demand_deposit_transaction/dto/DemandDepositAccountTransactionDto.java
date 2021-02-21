package org.kodluyoruz.mybank.demand_deposit_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountTransactionDto {
    private String toIban;

    private String total;

}
