package org.kodluyoruz.mybank.credit_card_transaction;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDtoReturn {
    private LocalDateTime dateTime;
    private double total;
}
