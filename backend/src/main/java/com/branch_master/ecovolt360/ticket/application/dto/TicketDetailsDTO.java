package com.branch_master.ecovolt360.ticket.application.dto;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;

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
        Integer evaluate,
        String evaluateComment,
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
            ticket.getEvaluate(),
            ticket.getEvaluateComment(),
            ticket.getCreatedAt()
        );
    }
}
