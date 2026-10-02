package com.branch_master.ecovolt360.address.infrastructure.persistence;

import com.branch_master.ecovolt360.address.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAddressRepository
        extends JpaRepository<Address, UUID> {
}
