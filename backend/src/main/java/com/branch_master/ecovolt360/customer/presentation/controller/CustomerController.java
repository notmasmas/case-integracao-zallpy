package com.branch_master.ecovolt360.customer.presentation.controller;

import com.branch_master.ecovolt360.customer.application.dto.CustomerBodyDTO;
import com.branch_master.ecovolt360.customer.application.dto.CustomerDetailsDTO;
import com.branch_master.ecovolt360.customer.application.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDetailsDTO> createCustomer(@RequestBody @Valid CustomerBodyDTO customer,
                                                             UriComponentsBuilder uriBuilder) {
        CustomerDetailsDTO newCustomer = customerService.processCustomer(customer);
        URI uri = uriBuilder.path("/customers/{id}").buildAndExpand(newCustomer.id()).toUri();

        return ResponseEntity.created(uri).body(newCustomer);
    }
}
