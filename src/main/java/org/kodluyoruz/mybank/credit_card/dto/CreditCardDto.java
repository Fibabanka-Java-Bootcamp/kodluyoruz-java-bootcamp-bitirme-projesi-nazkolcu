package org.kodluyoruz.mybank.credit_card.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {
    private int password;
    private String cardLimit;
}
