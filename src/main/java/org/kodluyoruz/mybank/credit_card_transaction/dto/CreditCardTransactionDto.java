package org.kodluyoruz.mybank.credit_card_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDto {
    private String toIban;
    private String total;
    private int password;
    private int cvc;

}
