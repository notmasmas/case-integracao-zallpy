package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyEvaluateDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@Validated
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TicketDetailsDTO> createTicket(@RequestBody @Valid TicketBodyDTO ticket,
                                                         @CurrentUser AuthenticatedUser currentUser,
                                                         UriComponentsBuilder uriBuilder) {
        TicketDetailsDTO newTicket = ticketService.processTicket(currentUser.userId(), ticket);
        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(newTicket.id()).toUri();

        return ResponseEntity.created(uri).body(newTicket);
    }

    @GetMapping
    public ResponseEntity<List<TicketSummaryDTO>> listTickets(@CurrentUser AuthenticatedUser currentUser) {
        return ResponseEntity.ok(ticketService.listCustomerTickets(currentUser.userId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailsDTO> getTicket(@PathVariable UUID id,
                                                      @RequestParam(defaultValue = "0") @Min(0) int page,
                                                      @CurrentUser AuthenticatedUser currentUser) {
        TicketDetailsDTO ticket = ticketService.getCustomerTicket(currentUser.userId(), id, page);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/evaluation")
    public ResponseEntity<TicketDetailsDTO> evaluateTicket(@PathVariable UUID id,
                                                          @RequestBody @Valid TicketBodyEvaluateDTO ticketBodyEvaluateDTO,
                                                          @CurrentUser AuthenticatedUser currentUser) {
        TicketDetailsDTO ticket = ticketService.evaluateTicket(currentUser.userId(), id, ticketBodyEvaluateDTO);
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDetailsDTO> updateStatus(@PathVariable UUID id,
                                                          @RequestBody @Valid TicketBodyStatusDTO ticketBodyStatusDTO,
                                                          @CurrentUser AuthenticatedUser currentUser) {
        TicketDetailsDTO ticket = ticketService.updateStatus(currentUser.userId(), id, ticketBodyStatusDTO);
        return ResponseEntity.ok(ticket);
    }
}
