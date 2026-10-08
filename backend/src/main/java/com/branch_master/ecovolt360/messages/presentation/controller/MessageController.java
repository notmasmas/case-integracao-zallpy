package com.branch_master.ecovolt360.messages.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessagePageDTO;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/tickets/{ticketId}/messages")
@Validated
public class MessageController {

    private final MessageService messageService;
    private final TicketService ticketService;

    public MessageController(MessageService messageService, TicketService ticketService) {
        this.messageService = messageService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<MessagePageDTO> listMessages(@PathVariable UUID ticketId,
                                                       @RequestParam(defaultValue = "0") @Min(0) int page,
                                                       @CurrentUser AuthenticatedUser currentUser) {
        MessagePageDTO messages = ticketService.listCustomerMessages(currentUser.userId(), ticketId, page);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@PathVariable UUID ticketId,
                                                    @RequestBody @Valid MessageBodyDTO messageBodyDTO,
                                                    @CurrentUser AuthenticatedUser currentUser,
                                                    UriComponentsBuilder uriBuilder) {
        MessageDTO messageDTO = messageService.processMessage(
                currentUser.userId(),
                currentUser.role(),
                ticketId,
                messageBodyDTO
        );
        URI uri = uriBuilder
                .path("/tickets/{ticketId}/messages/{id}")
                .buildAndExpand(ticketId, messageDTO.id())
                .toUri();
        return ResponseEntity.created(uri).body(messageDTO);
    }
}
