package com.branch_master.ecovolt360.ticket;

import com.branch_master.ecovolt360.ticket.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.dto.TicketDetailsDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @Transactional
    public ResponseEntity<TicketDetailsDTO> createTicket(@RequestBody @Valid TicketBodyDTO ticket,
                                                         UriComponentsBuilder uriBuilder) {
        UUID customerId = UUID.randomUUID(); // temp

        TicketDetailsDTO newTicket = ticketService.processTicket(customerId, ticket);
        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(newTicket.id()).toUri();

        return ResponseEntity.created(uri).body(newTicket);
    }
}
