package com.branch_master.ecovolt360.user.application.dto;

import com.branch_master.ecovolt360.address.application.dto.AddressBodyDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserBodyDTO (

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=100, message = "O nome deve ter no máximo 100 caracteres.")
        String name,

        @NotBlank(message = "Campo obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max=30, message = "O e-mail deve ter no máximo 30 caracteres.")
        String email,

        @NotBlank(message = "Campo obrigatório.")
        @Size(min = 8, max = 25, message = "A senha deve ter entre 8 e 25 caracteres.")
        @Pattern(regexp = "^(?=.*\\p{L})(?=.*\\d).+$", message = "A senha deve conter letras e números.")
        String password,

        @NotBlank(message = "Campo obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos, apenas números.")
        String cpf,

        @NotNull(message = "Campo obrigatório.")
        @Valid
        AddressBodyDTO address) {
}
