package com.branch_master.ecovolt360.project.application.dto;

import com.branch_master.ecovolt360.address.domain.entity.Address;

public record ProjectAddressDTO(
        String cep,
        String state,
        String city,
        String neighborhood,
        String street,
        String number,
        String complement
) {
    public static ProjectAddressDTO from(Address address) {
        if (address == null) {
            return null;
        }
        return new ProjectAddressDTO(
                address.getCep(),
                address.getState(),
                address.getCity(),
                address.getNeighborhood(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement()
        );
    }
}
