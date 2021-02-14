package org.kodluyoruz.mybank.saving_transaction;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountTransactionDto {
    private String toIban;
    private double total;
}
