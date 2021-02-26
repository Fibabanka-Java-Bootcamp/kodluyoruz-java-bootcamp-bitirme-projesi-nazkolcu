package org.kodluyoruz.mybank.credit_card.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDtoWithDebt {
    @Expose
    private Long cardNumber;
    @Expose
    private LocalDate expirationDate;
    @Expose
    private double debt;
    @Expose
    private double cardLimit;
    @Expose
    private int password;
    @Expose
    private int cvv;
}
