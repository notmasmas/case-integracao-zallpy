package com.branch_master.ecovolt360.customer.application.dto;

import com.branch_master.ecovolt360.customer.domain.entity.Customer;

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
