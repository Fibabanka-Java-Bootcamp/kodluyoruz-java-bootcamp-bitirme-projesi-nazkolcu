package org.kodluyoruz.mybank.credit_card_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDtoWithoutIban {
    private String total;
    private int password;
}
