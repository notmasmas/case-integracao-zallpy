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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CustomerRepository customerRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, ticketRepository, customerRepository);
    }

    @Test
    void shouldCreateMessageForOpenCustomerTicket() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID messageId = UUID.fromString("77777777-7777-7777-7777-777777777777");
        Customer customer = new Customer(userId);
        customer.setId(customerId);
        Ticket ticket = new Ticket(customerId, UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Chamado", "Descricao");
        ticket.setId(ticketId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(messageId);
            return message;
        });

        MessageDTO result = messageService.processMessage(
                userId,
                Role.CUSTOMER,
                ticketId,
                new MessageBodyDTO("Inversor voltou a comunicar")
        );

        assertEquals(messageId, result.id());
        assertEquals(userId, result.senderUserId());
        assertEquals(Role.CUSTOMER, result.role());
        assertEquals("Inversor voltou a comunicar", result.content());
    }

    @Test
    void shouldRejectMessageWhenUserIsNotCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
                ForbiddenException.class,
                () -> messageService.processMessage(userId, Role.CUSTOMER, ticketId, new MessageBodyDTO("Oi"))
        );
        verify(messageRepository, never()).save(any());
    }

    @Test
    void shouldRejectMessageWhenTicketDoesNotBelongToCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = new Customer(userId);
        customer.setId(customerId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> messageService.processMessage(userId, Role.CUSTOMER, ticketId, new MessageBodyDTO("Oi"))
        );
    }

    @ParameterizedTest
    @EnumSource(value = TicketStatus.class, names = {"RESOLVED", "CLOSED"})
    void shouldRejectMessageWhenTicketDoesNotAcceptNewMessages(TicketStatus status) {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = new Customer(userId);
        customer.setId(customerId);
        Ticket ticket = new Ticket(customerId, UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Chamado", "Descricao");
        ticket.setId(ticketId);
        ticket.setStatus(status);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(ticket));

        assertThrows(
                TicketClosedException.class,
                () -> messageService.processMessage(userId, Role.CUSTOMER, ticketId, new MessageBodyDTO("Oi"))
        );
        verify(messageRepository, never()).save(any());
    }
}
