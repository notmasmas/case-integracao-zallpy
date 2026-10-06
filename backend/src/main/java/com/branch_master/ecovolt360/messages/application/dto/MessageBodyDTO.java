package com.branch_master.ecovolt360.messages.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MessageBodyDTO(
        @NotNull
        UUID ticketId,

        @NotBlank
        @Size(max = 150)
        String content
) {
}
