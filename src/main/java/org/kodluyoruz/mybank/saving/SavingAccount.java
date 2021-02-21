package org.kodluyoruz.mybank.saving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.customer.Customer;
import org.kodluyoruz.mybank.saving.dto.SavingAccountDtoReturn;
import org.kodluyoruz.mybank.saving_balance.SavingAccountBalance;
import org.kodluyoruz.mybank.saving_transaction.SavingAccountTransaction;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saving_account")
public class SavingAccount {

    @Id
    private String iban;

    @ManyToOne
    @JoinColumn(name = "customerNumber", referencedColumnName = "customerNumber")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_id", referencedColumnName = "id")
    private SavingAccountBalance balance;

    @OneToMany(mappedBy = "savingAccount")
    private List<SavingAccountTransaction> transactions;


    public SavingAccountDtoReturn toSavingAccountDtoReturn() {
        return SavingAccountDtoReturn.builder()
                .iban(this.iban)
                .build();
    }



}