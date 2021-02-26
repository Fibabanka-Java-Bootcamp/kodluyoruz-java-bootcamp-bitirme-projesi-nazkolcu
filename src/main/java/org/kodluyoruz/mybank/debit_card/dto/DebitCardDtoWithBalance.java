package org.kodluyoruz.mybank.debit_card.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitCardDtoWithBalance {
    @Expose
    private Long cardNumber;
    @Expose
    private LocalDate expirationDate;
    @Expose
    private int password;
    @Expose
    private int cvv;
    @Expose
    private double amount ;
    @Expose
    private String currency;
}
