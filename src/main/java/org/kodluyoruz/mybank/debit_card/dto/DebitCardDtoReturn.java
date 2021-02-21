package org.kodluyoruz.mybank.debit_card.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDtoReturn {
    private Long cardNumber;
    private int cvv;
}
