package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;

import java.util.UUID;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketDetailsDTO processTicket(UUID customerId, TicketBodyDTO ticketDTO) {
        Ticket newTicket = ticketRepository.save(new Ticket(
                customerId,
                ticketDTO.projectId(),
                ticketDTO.title(),
                ticketDTO.description()
        ));
        return new TicketDetailsDTO(newTicket);
    }
}
