package org.kodluyoruz.mybank.customer.dto;

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
public class CustomerDtoWithCustomerNumber {
    private Long customerNumber;
    private String tckn;
    private String name;
    private String surname;
    private LocalDate birthDate;


}
