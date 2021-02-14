package org.kodluyoruz.mybank.credit_card_transaction;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDtoWithoutIban {
    private double total;
    private int password;
}
