package org.kodluyoruz.mybank.saving.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountDtoReturn {
    private String iban;

}