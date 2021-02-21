package org.kodluyoruz.mybank.debit_card_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardTransactionDtoWithoutIban {
    private String total;
    private int password;
}
