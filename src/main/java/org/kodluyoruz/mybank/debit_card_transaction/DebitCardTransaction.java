package org.kodluyoruz.mybank.debit_card_transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.debit_card.DebitCard;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoReturn;
import org.kodluyoruz.mybank.debit_card_transaction.dto.DebitCardTransactionDtoStatement;
import org.kodluyoruz.mybank.demand_deposit_transaction.dto.DemandDepositAccountTransactionDtoStatement;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_card_transaction")
public class DebitCardTransaction {
    @Id
    @GeneratedValue
    private Integer id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;

    private double total;

    @ManyToOne (cascade= {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "from_cardNumber", referencedColumnName = "cardNumber")
    private DebitCard debitCard;

    private String toIban;
    private String flowType;

    public DebitCardTransactionDtoReturn toDebitCardTransactionDtoReturn() {
        return DebitCardTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }
    public DebitCardTransactionDtoStatement toDebitCardTransactionDtoStatement() {
        return DebitCardTransactionDtoStatement.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .toIban(this.toIban)
                .flowType(this.flowType).build();
    }
}
