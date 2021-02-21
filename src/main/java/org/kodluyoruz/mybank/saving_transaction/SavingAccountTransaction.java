package org.kodluyoruz.mybank.saving_transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.kodluyoruz.mybank.saving_transaction.dto.SavingAccountTransactionDtoReturn;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saving_account_transaction")
public class SavingAccountTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;

    private double total;

    @ManyToOne (cascade= {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "from_iban", referencedColumnName = "iban")
    private SavingAccount savingAccount;


    private String toIban;

    private String flowType;

    public SavingAccountTransactionDtoReturn toSavingAccountTransactionDtoReturn() {
        return SavingAccountTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }
}
