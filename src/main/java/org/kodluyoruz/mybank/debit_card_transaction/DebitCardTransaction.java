package org.kodluyoruz.mybank.debit_card_transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.debit_card.DebitCard;
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

    @ManyToOne
    @JoinColumn(name = "from_cardNumber", referencedColumnName = "cardNumber")
    private DebitCard debitCard;

    private String toIban;

    public DebitCardTransactionDtoReturn toDebitCardTransactionDtoReturn() {
        return DebitCardTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }
}
