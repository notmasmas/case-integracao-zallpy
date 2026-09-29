package com.branch_master.ecovolt360.customer.dto;

import com.branch_master.ecovolt360.customer.Customer;

import java.util.UUID;

public record CustomerDetailsDTO (
        UUID id,
        UUID userId
) {
    public CustomerDetailsDTO(Customer customer) {
        this(
            customer.getId(),
            customer.getUserId()
        );
    }
}
