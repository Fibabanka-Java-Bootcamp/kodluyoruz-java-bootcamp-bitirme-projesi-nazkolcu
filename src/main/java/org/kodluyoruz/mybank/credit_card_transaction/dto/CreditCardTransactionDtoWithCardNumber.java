package org.kodluyoruz.mybank.credit_card_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDtoWithCardNumber {
    private Long toCardNumber;
    private String debt;

}
