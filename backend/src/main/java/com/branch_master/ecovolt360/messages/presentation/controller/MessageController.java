package com.branch_master.ecovolt360.messages.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/tickets/{ticketId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
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
