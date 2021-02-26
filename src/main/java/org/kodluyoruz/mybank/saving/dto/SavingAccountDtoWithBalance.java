package org.kodluyoruz.mybank.saving.dto;

import com.google.gson.annotations.Expose;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountDtoWithBalance {
    @Expose
    private String iban;
    @Expose
    private double amount ;
    @Expose
    private String currency;
}
