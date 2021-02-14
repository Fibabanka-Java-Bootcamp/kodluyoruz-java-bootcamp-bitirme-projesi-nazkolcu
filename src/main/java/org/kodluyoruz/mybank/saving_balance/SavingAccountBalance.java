package org.kodluyoruz.mybank.saving_balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.saving.SavingAccount;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saving_account_balance")
public class SavingAccountBalance {
    @Id
    @GeneratedValue
    private Integer id;

    private double amount = 0.0;
    private String currency;
    @OneToOne(mappedBy = "balance")
    private SavingAccount savingAccount;

    public SavingAccountDtoReturnBalance toSavingAccountDtoReturnBalance() {
        return SavingAccountDtoReturnBalance.builder()
                .amount(this.amount)
                .build();
    }

}