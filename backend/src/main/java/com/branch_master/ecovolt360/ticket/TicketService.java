package com.branch_master.ecovolt360.ticket;

import com.branch_master.ecovolt360.ticket.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.dto.TicketDetailsDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsDTO processTicket(UUID customerId, @Valid TicketBodyDTO ticketDTO) {

        Ticket newTicket = ticketRepository.save(new Ticket(customerId, ticketDTO));
        return new TicketDetailsDTO(newTicket);
    }
}
