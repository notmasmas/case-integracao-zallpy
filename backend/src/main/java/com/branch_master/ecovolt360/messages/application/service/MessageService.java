package com.branch_master.ecovolt360.messages.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.ticket.application.exception.TicketClosedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    public MessageService(
            MessageRepository messageRepository,
            TicketRepository ticketRepository,
            CustomerRepository customerRepository
    ) {
        this.messageRepository = messageRepository;
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
    }

    public MessageDTO processMessage(UUID userId, Role role, UUID ticketId, MessageBodyDTO messageDTO) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(ForbiddenException::new);
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customer.getId())
                .orElseThrow(TicketNotFoundException::new);
        if (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosedException();
        }
        Message message = messageRepository.save(new Message(
                ticket.getId(),
                userId,
                messageDTO.content(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        ));
        return new MessageDTO(message, role);
    }
}
