package com.branch_master.ecovolt360.ticket.application.dto;

import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record TicketDetailsDTO(
        UUID id,
        UUID customerId,
        UUID supportId,
        UUID projectId,
        String title,
        String description,
        TicketCategory category,
        TicketStatus status,
        List<MessageDTO> messages,
        boolean hasOlderMessages,
        Integer evaluate,
        String evaluateComment,
        boolean isEvaluated,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public TicketDetailsDTO(Ticket ticket, List<MessageDTO> messages) {
        this(ticket, messages, false);
    }

    public TicketDetailsDTO(Ticket ticket, List<MessageDTO> messages, boolean hasOlderMessages) {
        this(
                ticket.getId(),
                ticket.getCustomerId(),
                ticket.getSupportId(),
                ticket.getProjectId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getStatus(),
                messages,
                hasOlderMessages,
                ticket.getEvaluate(),
                ticket.getEvaluateComment(),
                ticket.isEvaluated(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
