package org.kodluyoruz.mybank.credit_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDtoReturn;
import org.kodluyoruz.mybank.credit_card.dto.CreditCardDtoReturnDebt;
import org.kodluyoruz.mybank.credit_card_transaction.CreditCardTransaction;
import org.kodluyoruz.mybank.customer.Customer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card")
public class CreditCard {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-1")
    @GenericGenerator(
            name = "sequence-generator-1",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "credit_card_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "510379300"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long cardNumber;

    @OneToOne(fetch = FetchType.LAZY)//(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "customer_number", referencedColumnName = "customerNumber")
    private Customer customer;

    @DateTimeFormat(pattern = "MM-YY")
    private LocalDate expirationDate;

    @Column(length = 4)
    private int password;

    @OneToMany(mappedBy = "creditCard")
    private List<CreditCardTransaction> creditCardTransactions;

    private double debt = 0.0;

    private double cardLimit;

    private int cvv;

    public CreditCardDtoReturn toCreditCardDtoReturn() {
        return CreditCardDtoReturn.builder()
                .cardNumber(this.cardNumber)
                .cvv(this.cvv)
                .build();
    }

    public CreditCardDtoReturnDebt toCreditCardDtoReturnDebt() {
        return CreditCardDtoReturnDebt.builder()
                .debt(this.debt)
                .build();
    }
}
