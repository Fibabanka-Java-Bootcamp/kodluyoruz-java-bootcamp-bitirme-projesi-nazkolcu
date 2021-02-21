package org.kodluyoruz.mybank.debit_card_transaction.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardTransactionDtoReturn {
    private LocalDateTime dateTime;
    private double total;
}
