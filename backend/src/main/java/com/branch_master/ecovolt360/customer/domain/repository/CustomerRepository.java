package com.branch_master.ecovolt360.customer.domain.repository;

import com.branch_master.ecovolt360.customer.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    boolean existsByUserId(UUID userId);

    Customer save(Customer customer);

    Optional<Customer> findByUserId(UUID userId);
}
