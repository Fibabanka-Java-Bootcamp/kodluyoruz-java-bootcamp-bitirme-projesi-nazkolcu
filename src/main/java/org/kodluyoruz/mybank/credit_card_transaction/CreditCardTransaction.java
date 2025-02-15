package org.kodluyoruz.mybank.credit_card_transaction;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoExtract;
import org.kodluyoruz.mybank.credit_card_transaction.dto.CreditCardTransactionDtoReturn;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_transaction")
public class CreditCardTransaction {

    @Id
    @GeneratedValue
    private Integer id;
    @Expose
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;
    @Expose
    private double total;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "from_cardNumber", referencedColumnName = "cardNumber")
    private CreditCard creditCard;
    @Expose
    private String toIban;
    private String flowType;

    public CreditCardTransactionDtoReturn toCreditCardTransactionDtoReturn() {
        return CreditCardTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }

    public CreditCardTransactionDtoExtract toCreditCardTransactionDtoExtract() {
        return CreditCardTransactionDtoExtract.builder()
                . dateTime(this.dateTime)
                .total(this.total)
                .toIban(this.toIban)
                .build();
    }
}
