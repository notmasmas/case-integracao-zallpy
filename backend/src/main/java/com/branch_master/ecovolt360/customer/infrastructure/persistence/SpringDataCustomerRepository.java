package com.branch_master.ecovolt360.customer.infrastructure.persistence;

import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataCustomerRepository
        extends JpaRepository<Customer, UUID> {

    boolean existsByUserId(UUID userId);
}
