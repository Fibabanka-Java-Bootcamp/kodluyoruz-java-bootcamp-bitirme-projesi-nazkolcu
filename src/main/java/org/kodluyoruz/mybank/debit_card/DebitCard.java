package org.kodluyoruz.mybank.debit_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.kodluyoruz.mybank.debit_card_transaction.DebitCardTransaction;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_card")
public class DebitCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator-2")
    @GenericGenerator(
            name = "sequence-generator-2",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "debit_card_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "510379200"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long cardNumber;

    @OneToOne
    @JoinColumn(name = "account_iban", referencedColumnName = "iban")
    private DemandDepositAccount demandDepositAccount;

    @DateTimeFormat(pattern = "MM-YY")
    private LocalDate expirationDate;

    @Column(length = 4)
    private int password;


    @OneToMany(mappedBy = "debitCard")
    private List<DebitCardTransaction> debitCardTransactions;


    public DebitCardDtoReturn toDebitCardDtoReturn() {
        return DebitCardDtoReturn.builder()
                .cardNumber(this.cardNumber)
                .build();
    }
}