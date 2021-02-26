package org.kodluyoruz.mybank.demand_deposit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoReturn;
import org.kodluyoruz.mybank.demand_deposit.dto.DemandDepositAccountDtoWithBalance;
import org.kodluyoruz.mybank.demand_deposit_balance.DemandDepositAccountBalance;
import org.kodluyoruz.mybank.demand_deposit_transaction.DemandDepositAccountTransaction;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_deposit_account")
public class DemandDepositAccount {

    @Id
    private String iban;

    @ManyToOne
    @JoinColumn(name = "customerNumber", referencedColumnName = "customerNumber")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "balance_id", referencedColumnName = "id")
    private DemandDepositAccountBalance balance;


    @OneToMany(mappedBy = "demandDepositAccount")
    private List<DemandDepositAccountTransaction> transactions;

    @OneToOne(mappedBy = "demandDepositAccount")
    private DebitCard debitCard;

    public DemandDepositAccountDtoReturn toDemandDepositAccountDtoReturn() {
        return DemandDepositAccountDtoReturn.builder()
                .iban(this.iban)
                .build();
    }

    public DemandDepositAccountDtoWithBalance toDemandDepositAccountDtoWithBalance() {
        return DemandDepositAccountDtoWithBalance.builder()
                .iban(this.iban)
                .amount(this.balance.getAmount())
                .currency(this.balance.getCurrency()).build();

    }
}
