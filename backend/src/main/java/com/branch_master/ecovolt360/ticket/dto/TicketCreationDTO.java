package com.branch_master.ecovolt360.ticket.dto;

import java.util.UUID;

public record TicketCreationDTO (
        UUID projectId,
        UUID customerId,
        String title,
        String description) {

        public TicketCreationDTO(TicketBodyDTO ticketBodyDTO, UUID userId) {
            this(
                    ticketBodyDTO.projectId(),
                    userId,
                    ticketBodyDTO.title(),
                    ticketBodyDTO.description()
            );
        }
}
