package org.kodluyoruz.mybank.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.kodluyoruz.mybank.credit_card.CreditCard;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.dto.CustomerDtoReturn;
import org.kodluyoruz.mybank.demand_deposit.DemandDepositAccount;
import org.kodluyoruz.mybank.saving.SavingAccount;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "customer_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000000000"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long customerNumber;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "customer")//
    private List<SavingAccount> savingAccounts;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "customer")
    private List<DemandDepositAccount> demandDepositAccounts;

    @OneToOne(cascade = {CascadeType.REMOVE}, mappedBy = "customer")
    private CreditCard creditCard;

    private String name;

    private String surname;

    @DateTimeFormat(pattern = "dd/MM/yyyy") //
    private LocalDate birthDate;

    public String tckn;

    @Override
    public String toString() {
        return "Customer{" +
                "customerNumber=" + customerNumber +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", tckn='" + tckn + '\'' +
                '}';
    }

    public CustomerDto toCustomerDto() {
        return CustomerDto.builder()
                .name(this.name)
                .surname(this.surname)
                .birthDate(this.birthDate)
                .tckn(this.tckn)
                .build();
    }

    public CustomerDtoReturn toCustomerDtoReturn() {
        return CustomerDtoReturn.builder()
                .customerNumber(this.customerNumber)
                .name(this.name)
                .surname(this.surname)
                .build();
    }
}
