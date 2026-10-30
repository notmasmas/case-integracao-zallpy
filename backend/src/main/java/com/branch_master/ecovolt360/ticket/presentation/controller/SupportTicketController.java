package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyAssignDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/support/tickets")
public class SupportTicketController {

    private final TicketService ticketService;
    private final MessageService messageService;

    public SupportTicketController(TicketService ticketService, MessageService messageService) {
        this.ticketService = ticketService;
        this.messageService = messageService;
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

    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<TicketDetailsDTO> assignTicket(@PathVariable UUID ticketId,
                                                          @RequestBody @Valid TicketBodyAssignDTO ticketBodyAssignDTO,
                                                          @CurrentUser AuthenticatedUser currentUser) {
        TicketDetailsDTO ticket = ticketService.assignTicket(currentUser.userId(), ticketId, ticketBodyAssignDTO);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable UUID ticketId,
                                                    @RequestBody @Valid MessageBodyDTO messageBodyDTO,
                                                    @CurrentUser AuthenticatedUser currentUser,
                                                    UriComponentsBuilder uriBuilder) {
        MessageDTO messageDTO = messageService.processSupportMessage(
                currentUser.userId(),
                currentUser.role(),
                ticketId,
                messageBodyDTO
        );
        URI uri = uriBuilder
                .path("/support/tickets/{ticketId}/messages/{id}")
                .buildAndExpand(ticketId, messageDTO.id())
                .toUri();
        return ResponseEntity.created(uri).body(messageDTO);
    }
}
