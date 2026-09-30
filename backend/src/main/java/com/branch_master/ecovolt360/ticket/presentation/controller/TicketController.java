package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TicketDetailsDTO> createTicket(@RequestBody @Valid TicketBodyDTO ticket,
                                                         // @AuthenticationPrincipal UserPrincipal currentUser
                                                         UriComponentsBuilder uriBuilder) {
        // UUID customerId = currentUser.getId();
        UUID customerId = UUID.randomUUID(); // temp

        TicketDetailsDTO newTicket = ticketService.processTicket(customerId, ticket);
        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(newTicket.id()).toUri();

        return ResponseEntity.created(uri).body(newTicket);
    }
}
