package org.kodluyoruz.mybank.debit_card_transaction;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardTransactionDto {
    private String toIban;
    private int password;
    private double total;
}
