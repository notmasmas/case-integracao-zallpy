package com.branch_master.ecovolt360.user.application.dto;

import com.branch_master.ecovolt360.address.application.dto.AddressBodyDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserBodyDTO (

        @NotBlank
        @Size(max=100)
        String name,

        @NotBlank
        @Email
        @Size(max=30)
        String email,

        @NotBlank(message = "Campo obrigatório.")
        @Size(min = 8, max = 25, message = "A senha deve ter entre 8 e 25 caracteres.")
        @Pattern(regexp = "^(?=.*\\p{L})(?=.*\\d).+$", message = "A senha deve conter letras e números.")
        String password,

        @NotBlank
        @Pattern(regexp = "\\d{11}")
        String cpf,

        @NotNull
        @Valid
        AddressBodyDTO address) {
}
