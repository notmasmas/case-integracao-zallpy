package com.branch_master.ecovolt360.ticket.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketBodyEvaluateDTO(
        @NotNull
        @Min(1)
        @Max(5)
        Integer evaluation,

        @Size(max = 150)
        String comment
) {
}