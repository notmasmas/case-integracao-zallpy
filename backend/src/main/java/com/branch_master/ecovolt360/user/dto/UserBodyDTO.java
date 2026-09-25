package com.branch_master.ecovolt360.user.dto;

import com.branch_master.ecovolt360.address.dto.AddressBodyDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserBodyDTO (

        @NotBlank
        @Size(max=100)
        String name,

        @NotBlank
        @Email
        @Size(max=50)
        String email,

        @NotBlank
        @Size(max=100)
        String password,

        @NotNull
        @Valid
        AddressBodyDTO address) {
}
