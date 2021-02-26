package org.kodluyoruz.mybank.demand_deposit_transaction.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandDepositAccountTransactionDtoStatement {
    @Expose
    private LocalDateTime dateTime;
    @Expose
    private String toIban;
    @Expose
    private double total;
    @Expose
    private String flowType;

}
