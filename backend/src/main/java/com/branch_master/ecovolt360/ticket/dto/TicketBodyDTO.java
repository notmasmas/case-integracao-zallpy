package com.branch_master.ecovolt360.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TicketBodyDTO (

        @NotBlank
        UUID projectId,

        @NotBlank
        @Size(max=50)
        String title,

        @NotBlank
        @Size(max=150)
        String description) {
}
