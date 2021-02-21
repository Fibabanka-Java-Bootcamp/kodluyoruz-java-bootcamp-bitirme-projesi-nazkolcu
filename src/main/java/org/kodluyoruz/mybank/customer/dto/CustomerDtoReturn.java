package org.kodluyoruz.mybank.customer.dto;

import lombok.*;
import org.kodluyoruz.mybank.customer.Customer;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDtoReturn {
    private Long customerNumber;
    private String name;
    private String surname;

    public Customer toCustomer() {
        return Customer.builder()
                .customerNumber(this.customerNumber)
                .name(this.name)
                .surname(this.surname)
                .build();
    }
}
