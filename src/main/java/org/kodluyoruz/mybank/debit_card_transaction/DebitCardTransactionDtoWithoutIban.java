package org.kodluyoruz.mybank.debit_card_transaction;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardTransactionDtoWithoutIban {
    private double total;
    private int password;
}
