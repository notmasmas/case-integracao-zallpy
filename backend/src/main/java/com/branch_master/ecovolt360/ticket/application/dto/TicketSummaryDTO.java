package com.branch_master.ecovolt360.ticket.application.dto;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;

import java.util.UUID;

public record TicketSummaryDTO(
        UUID id,
        UUID customerId,
        UUID supportId,
        UUID projectId,
        String title,
        String description
) {
    public TicketSummaryDTO(Ticket ticket) {
        this(
                ticket.getId(),
                ticket.getCustomerId(),
                ticket.getSupportId(),
                ticket.getProjectId(),
                ticket.getTitle(),
                ticket.getDescription()
        );
    }
}
