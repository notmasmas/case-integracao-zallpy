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
        String title,
        String description,
        TicketCategory category,
        TicketStatus status,
        String supportName,
        List<MessageDTO> messages,
        int page,
        int totalPages,
        int totalMessages,
        Integer evaluate,
        String evaluateComment,
        boolean isEvaluated,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public TicketDetailsDTO(
            Ticket ticket,
            String supportName,
            List<MessageDTO> messages,
            int page,
            int totalPages,
            int totalMessages
    ) {
        this(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getStatus(),
                supportName,
                messages,
                page,
                totalPages,
                totalMessages,
                ticket.getEvaluate(),
                ticket.getEvaluateComment(),
                ticket.isEvaluated(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
