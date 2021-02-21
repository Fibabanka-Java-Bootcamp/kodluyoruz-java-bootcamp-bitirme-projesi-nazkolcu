package org.kodluyoruz.mybank.customer.dto;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDtoWithoutTckn {
    @NotBlank(message = "Name for the customer is mandatory")
    private String name;
    @NotBlank(message = "Surname for the customer is mandatory")
    private String surname;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;


    public Customer toCustomer() {
        return Customer.builder()
                .name(this.name)
                .surname(this.surname)
                .birthDate(this.birthDate)
                .tckn(this.tckn)
                .build();
    }
}
