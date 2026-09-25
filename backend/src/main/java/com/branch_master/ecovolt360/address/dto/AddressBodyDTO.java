package com.branch_master.ecovolt360.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressBodyDTO (

        @NotBlank
        @Size(max=10)
        String cep,

        @NotBlank
        @Size(max=2)
        String state,

        @NotBlank
        @Size(max=100)
        String city,

        @NotBlank
        @Size(max=100)
        String neighborhood,

        @NotBlank
        @Size(max=100)
        String street,

        @NotBlank
        @Size(max=20)
        String number,

        @Size(max=100)
        String complement) {
}
