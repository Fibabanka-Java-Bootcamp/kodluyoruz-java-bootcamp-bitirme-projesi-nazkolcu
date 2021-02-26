package org.kodluyoruz.mybank.debit_card_transaction.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardTransactionDtoStatement {
    @Expose
    private LocalDateTime dateTime;

    @Expose
    private String toIban;
    @Expose
    private double total;
    @Expose
    private String flowType;

}
