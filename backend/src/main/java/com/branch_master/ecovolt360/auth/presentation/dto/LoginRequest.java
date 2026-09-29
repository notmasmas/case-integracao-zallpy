package com.branch_master.ecovolt360.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message= "Informe um e-mail válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String password
) {


    @Override
    public String toString() {
        return "LoginRequest[credentials]";
    }
}