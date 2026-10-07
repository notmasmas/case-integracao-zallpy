package com.branch_master.ecovolt360.messages.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageBodyDTO(
        @NotBlank
        @Size(max = 150)
        String content
) {
}
