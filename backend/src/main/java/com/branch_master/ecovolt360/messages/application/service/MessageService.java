package com.branch_master.ecovolt360.messages.application.service;

import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;

    public MessageService(MessageRepository messageRepository, TicketRepository ticketRepository) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
    }

    public MessageDTO processMessage(UUID userId, Role role, MessageBodyDTO messageDTO) {
        Ticket ticket = ticketRepository.findById(messageDTO.ticketId())
                .orElseThrow(TicketNotFoundException::new);
        Message message = messageRepository.save(new Message(
                ticket.getId(),
                userId,
                messageDTO.content(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        ));
        return new MessageDTO(message, role);
    }
}
