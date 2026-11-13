package com.branch_master.ecovolt360.address.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressBodyDTO (

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=10, message = "Máximo de 10 caracteres.")
        String cep,

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=2, message = "Máximo de 2 caracteres.")
        String state,

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=100, message = "Máximo de 100 caracteres.")
        String city,

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=100, message = "Máximo de 100 caracteres.")
        String neighborhood,

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=100, message = "Máximo de 100 caracteres.")
        String street,

        @NotBlank(message = "Campo obrigatório.")
        @Size(max=20, message = "Máximo de 20 caracteres.")
        String number,

        @Size(max=100, message = "Máximo de 100 caracteres.")
        String complement) {
}
