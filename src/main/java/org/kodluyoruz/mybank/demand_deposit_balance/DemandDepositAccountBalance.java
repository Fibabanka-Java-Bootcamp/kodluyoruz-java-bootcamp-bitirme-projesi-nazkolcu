package org.kodluyoruz.mybank.demand_deposit_balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_deposit_account_balance")
public class DemandDepositAccountBalance {
    @Id
    @GeneratedValue
    private Integer id;

    private double amount = 0.0;
    private String currency;
    @OneToOne(mappedBy = "balance")
    private DemandDepositAccount demandDepositAccount;

    public DemandDepositAccountDtoReturnBalance toDemandDepositAccountDtoReturnBalance() {
        return DemandDepositAccountDtoReturnBalance.builder()
                .amount(this.amount)
                .build();
    }
}
