package com.branch_master.ecovolt360.customer.infrastructure.persistence;

import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository repository;

    public JpaCustomerRepository(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public Optional<Customer> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}
