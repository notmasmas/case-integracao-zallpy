package com.branch_master.ecovolt360.address.domain.repository;

import com.branch_master.ecovolt360.address.domain.entity.Address;

public interface AddressRepository {
    Address save(Address address);
}
