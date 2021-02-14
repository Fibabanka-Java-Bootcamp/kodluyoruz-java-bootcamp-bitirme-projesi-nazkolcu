package org.kodluyoruz.mybank.saving_transaction;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountTransactionDtoReturn {
    private LocalDateTime dateTime;
    private double total;
}
