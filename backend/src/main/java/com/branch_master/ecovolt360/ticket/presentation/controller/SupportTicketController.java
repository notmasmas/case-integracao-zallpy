package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/support/tickets")
public class SupportTicketController {

    private final TicketService ticketService;

    public SupportTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketSummaryDTO>> listTickets(@CurrentUser AuthenticatedUser currentUser) {
        return ResponseEntity.ok(ticketService.listSupportTickets(currentUser.userId()));
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketDetailsDTO> updateStatus(@PathVariable UUID ticketId,
                                                          @RequestBody @Valid TicketBodyStatusDTO ticketBodyStatusDTO,
                                                          @CurrentUser AuthenticatedUser currentUser) {
        TicketDetailsDTO ticket = ticketService.updateStatus(currentUser.userId(), ticketId, ticketBodyStatusDTO);
        return ResponseEntity.ok(ticket);
    }
}
