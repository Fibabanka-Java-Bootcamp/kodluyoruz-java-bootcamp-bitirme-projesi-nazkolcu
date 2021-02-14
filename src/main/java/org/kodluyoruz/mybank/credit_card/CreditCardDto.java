package org.kodluyoruz.mybank.credit_card;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {

    private int password;
    private double cardLimit;
}
