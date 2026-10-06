package com.branch_master.ecovolt360.address.infrastructure.persistence;

import com.branch_master.ecovolt360.address.domain.entity.Address;
import com.branch_master.ecovolt360.address.domain.repository.AddressRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAddressRepository implements AddressRepository {

    private final SpringDataAddressRepository repository;

    public JpaAddressRepository(SpringDataAddressRepository repository) {
        this.repository = repository;
    }

    @Override
    public Address save(Address address) {
        return repository.save(address);
    }
}
