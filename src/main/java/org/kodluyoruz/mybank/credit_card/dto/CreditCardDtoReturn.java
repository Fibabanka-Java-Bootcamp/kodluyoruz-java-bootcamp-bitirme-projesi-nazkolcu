package org.kodluyoruz.mybank.credit_card.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDtoReturn {
    private Long cardNumber;
    private int cvv;
}
