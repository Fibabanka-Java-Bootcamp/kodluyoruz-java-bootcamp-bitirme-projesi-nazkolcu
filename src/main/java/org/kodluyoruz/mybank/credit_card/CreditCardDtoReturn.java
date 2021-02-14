package org.kodluyoruz.mybank.credit_card;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDtoReturn {
    private Long cardNumber;
}
