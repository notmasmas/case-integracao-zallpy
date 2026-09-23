package com.branch_master.ecovolt360.ticket.dto;

import com.branch_master.ecovolt360.ticket.TicketCategory;
import com.branch_master.ecovolt360.ticket.TicketStatus;

import java.time.LocalDateTime;
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
        LocalDateTime createdAt
) {
}
