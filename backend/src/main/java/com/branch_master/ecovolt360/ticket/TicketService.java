package com.branch_master.ecovolt360.ticket;

import com.branch_master.ecovolt360.ticket.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.dto.TicketCreationDTO;
import com.branch_master.ecovolt360.ticket.dto.TicketDetailsDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;


    public TicketDetailsDTO processTicket(String userId, @Valid TicketBodyDTO ticket) {



        Ticket newTicket = new Ticket();
    }
}
