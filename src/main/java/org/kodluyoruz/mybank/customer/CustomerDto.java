package org.kodluyoruz.mybank.customer;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    @NotBlank(message = "Name for the customer is mandatory")
    private String name;
    @NotBlank(message = "Surname for the customer is mandatory")
    private String surname;
    @DateTimeFormat(pattern = "dd/MM/yyyy") //@DateTimeFormat
    private LocalDate birthDate;
    @Size(min = 11, max = 11)
    private String tckn;


    public Customer toCustomer() {
        return Customer.builder()

                .name(this.name)
                .surname(this.surname)
                .birthDate(this.birthDate)
                .tckn(this.tckn)

                .build();
    }
}
