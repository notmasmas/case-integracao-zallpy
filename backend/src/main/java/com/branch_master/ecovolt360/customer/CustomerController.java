package com.branch_master.ecovolt360.customer;

import com.branch_master.ecovolt360.customer.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.dto.CustomerDetailsDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @Transactional
    public ResponseEntity<CustomerDetailsDTO> createCustomer(@RequestBody @Valid CustomerBodyDTO customer,
                                                             UriComponentsBuilder uriBuilder) {
        CustomerDetailsDTO newCustomer = customerService.processCustomer(customer);
        URI uri = uriBuilder.path("/customers/{id}").buildAndExpand(newCustomer.id()).toUri();

        return ResponseEntity.created(uri).body(newCustomer);
    }
}
