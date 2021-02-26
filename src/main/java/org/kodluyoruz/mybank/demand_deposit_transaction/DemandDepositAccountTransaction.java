package org.kodluyoruz.mybank.demand_deposit_transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoReturn;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demand_deposit_account_transaction")
public class DemandDepositAccountTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;

    private double total;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "from_iban", referencedColumnName = "iban")
    private DemandDepositAccount demandDepositAccount;


    private String toIban;
    private String flowType;

    public DemandDepositAccountTransactionDtoReturn toDemandDepositAccountTransactionDtoReturn() {
        return DemandDepositAccountTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }

    public DemandDepositAccountTransactionDtoStatement toDemandDepositAccountTransactionDtoStatement() {
        return DemandDepositAccountTransactionDtoStatement.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .toIban(this.toIban)
                .flowType(this.flowType).build();
    }


}
