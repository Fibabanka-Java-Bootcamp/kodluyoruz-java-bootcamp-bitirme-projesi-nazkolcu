package org.kodluyoruz.mybank.saving;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountDto {
    private String currency;
}