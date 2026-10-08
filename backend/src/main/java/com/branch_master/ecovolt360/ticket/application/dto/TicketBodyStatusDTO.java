package com.branch_master.ecovolt360.ticket.application.dto;

import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TicketBodyStatusDTO(
        @NotNull
        TicketStatus status
) {
}   