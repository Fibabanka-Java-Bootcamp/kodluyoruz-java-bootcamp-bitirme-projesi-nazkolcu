package org.kodluyoruz.mybank.saving_balance.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountDtoReturnBalance {
    private double amount;
}
