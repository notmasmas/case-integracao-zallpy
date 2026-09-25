package com.branch_master.ecovolt360.ticket.dto;

import com.branch_master.ecovolt360.ticket.Ticket;
import com.branch_master.ecovolt360.ticket.enums.TicketCategory;
import com.branch_master.ecovolt360.ticket.enums.TicketStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record TicketDetailsDTO (
        UUID id,
        UUID customerId,
        UUID supportId,
        UUID projectId,
        String title,
        String description,
        TicketCategory category,
        TicketStatus status,
        ZonedDateTime createdAt
) {
    public TicketDetailsDTO(Ticket ticket) {
        this(
            ticket.getId(),
            ticket.getCustomerId(),
            ticket.getSupportId(),
            ticket.getProjectId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getCategory(),
            ticket.getStatus(),
            ticket.getCreatedAt()
        );
    }
}
