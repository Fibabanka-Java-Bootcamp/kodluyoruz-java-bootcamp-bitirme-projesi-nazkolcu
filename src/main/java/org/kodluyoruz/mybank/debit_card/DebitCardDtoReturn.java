package org.kodluyoruz.mybank.debit_card;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDtoReturn {
    private Long cardNumber;
}
