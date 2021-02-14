package org.kodluyoruz.mybank.customer;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDtoReturn create(@Valid @RequestBody CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setSurname(customerDto.getSurname());
        customer.setBirthDate(customerDto.getBirthDate());
        customer.setTckn(customerDto.getTckn());
        return customerService.create(customer).toCustomerDtoReturn();
    }

    @GetMapping("/{customerNumber}")
    public CustomerDtoReturn get(@PathVariable("customerNumber") Long customerNumber) {
        return customerService.get(customerNumber).get().toCustomerDtoReturn();
   }

    @PutMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Optional<Customer> replaceCustomer(@Valid @RequestBody CustomerDto customerDto, @PathVariable("customerNumber") Long customerNumber) {

        return customerService.get(customerNumber)
                .map(customer -> {
                    customer.setName(customerDto.getName());
                    customer.setSurname(customerDto.getSurname());
                    customer.setBirthDate(customerDto.getBirthDate());
                    customer.setTckn(customerDto.getTckn());
                    return customerService.update(customer);
                });
    }

    @DeleteMapping("/{customerNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCustomer(@PathVariable("customerNumber") Long customerNumber) {
        customerService.delete(customerNumber);
    }

    @GetMapping(params = {"page", "size"})
    public List<CustomerDtoReturn> list(@Min(value = 0) @RequestParam("page") int page, @RequestParam("size") int size) {
        return customerService.list(PageRequest.of(page, size)).stream()
                .map(Customer::toCustomerDtoReturn)
                .collect(Collectors.toList());
    }
}
