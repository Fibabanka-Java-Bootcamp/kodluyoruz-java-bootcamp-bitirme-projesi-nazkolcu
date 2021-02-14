package org.kodluyoruz.mybank.credit_card_transaction;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kodluyoruz.mybank.credit_card.CreditCard;
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
    @Expose
    @Id
    @GeneratedValue
    private Integer id;
    @Expose
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dateTime;
    @Expose
    private double total;

    @ManyToOne
    @JoinColumn(name = "from_cardNumber", referencedColumnName = "cardNumber")
    private CreditCard creditCard;

    @Expose
    private String toIban;


    public CreditCardTransactionDtoReturn toCreditCardTransactionDtoReturn() {
        return CreditCardTransactionDtoReturn.builder()
                .dateTime(this.dateTime)
                .total(this.total)
                .build();
    }
}
