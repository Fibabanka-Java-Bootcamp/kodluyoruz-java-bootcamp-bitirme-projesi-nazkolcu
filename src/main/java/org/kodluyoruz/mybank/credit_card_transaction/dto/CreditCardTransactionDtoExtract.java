package org.kodluyoruz.mybank.credit_card_transaction.dto;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardTransactionDtoExtract {
    @Expose
    private LocalDateTime dateTime;
    @Expose
    private double total;
    @Expose
    private String toIban;
}
