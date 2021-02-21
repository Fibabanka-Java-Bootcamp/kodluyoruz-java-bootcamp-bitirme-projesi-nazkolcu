package org.kodluyoruz.mybank.saving_transaction.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountTransactionDto {
    private String toIban;
    private String total;

}
