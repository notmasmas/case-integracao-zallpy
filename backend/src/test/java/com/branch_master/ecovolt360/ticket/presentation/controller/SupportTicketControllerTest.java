package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.support.domain.entity.Support;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import com.branch_master.ecovolt360.ticket.presentation.exception.TicketExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketControllerTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupportRepository supportRepository;

    private MockMvc mockMvc;

    private final UUID userId = UUID.randomUUID();
    private final UUID ticketId = UUID.randomUUID();
    private Support support;

    @BeforeEach
    void setUp() {
        TicketService ticketService = new TicketService(
                ticketRepository,
                customerRepository,
                messageRepository,
                userRepository,
                supportRepository
        );
        MessageService messageService = new MessageService(
                messageRepository,
                ticketRepository,
                customerRepository,
                supportRepository
        );

        SupportTicketController controller = new SupportTicketController(ticketService, messageService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new TicketExceptionHandler(), new AccessExceptionHandler())
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .build();

        support = new Support(userId);
        support.setId(UUID.randomUUID());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new AuthenticatedUser(userId, Role.SUPPORT), null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Ticket ticket(UUID supportId, TicketStatus status) {
        Ticket ticket = new Ticket(UUID.randomUUID(), UUID.randomUUID(), "Inversor", "Inversor apitando");
        ticket.setId(ticketId);
        ticket.setSupportId(supportId);
        ticket.setStatus(status);
        return ticket;
    }

    private String assignBody(String category) {
        return """
                { "category": "%s" }
                """.formatted(category);
    }

    private String messageBody(String content) {
        return """
                { "content": "%s" }
                """.formatted(content);
    }

    @Test
    void shouldAssignTicketToAuthenticatedSupport() throws Exception {
        Ticket assigned = ticket(support.getId(), TicketStatus.PENDING);
        assigned.setCategory(TicketCategory.EQUIPMENT);

        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.assign(eq(ticketId), eq(support.getId()), eq(TicketCategory.EQUIPMENT), any(ZonedDateTime.class)))
                .thenReturn(1);
        when(ticketRepository.findByIdAndSupportId(ticketId, support.getId())).thenReturn(Optional.of(assigned));

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("EQUIPMENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.category").value("EQUIPMENT"));
    }

    @Test
    void shouldUpdateCategoryOfOwnTicket() throws Exception {
        Ticket updated = ticket(support.getId(), TicketStatus.IN_PROGRESS);
        updated.setCategory(TicketCategory.PAYMENT);

        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.assign(eq(ticketId), eq(support.getId()), eq(TicketCategory.PAYMENT), any(ZonedDateTime.class)))
                .thenReturn(1);
        when(ticketRepository.findByIdAndSupportId(ticketId, support.getId())).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("PAYMENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.category").value("PAYMENT"));

        verify(ticketRepository).findByIdAndSupportId(ticketId, support.getId());
        verify(ticketRepository, never()).findById(any());
    }

    @Test
    void shouldRejectAssignWhenTicketBelongsToAnotherSupport() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.assign(eq(ticketId), eq(support.getId()), eq(TicketCategory.PAYMENT), any(ZonedDateTime.class)))
                .thenReturn(0);
        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket(UUID.randomUUID(), TicketStatus.UNDER_REVIEW)));

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("PAYMENT")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_ALREADY_ASSIGNED"));
    }

    @Test
    void shouldReturnNotFoundWhenAssigningMissingTicket() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.assign(eq(ticketId), eq(support.getId()), eq(TicketCategory.OTHER), any(ZonedDateTime.class)))
                .thenReturn(0);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("OTHER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_FOUND"));
    }

    @Test
    void shouldRejectAssignWhenTicketIsClosed() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.assign(eq(ticketId), eq(support.getId()), eq(TicketCategory.OTHER), any(ZonedDateTime.class)))
                .thenReturn(0);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket(null, TicketStatus.CLOSED)));

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("OTHER")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_CLOSED"));
    }

    @Test
    void shouldRejectAssignWithoutCategory() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.category").exists());

        verifyNoInteractions(ticketRepository);
    }

    @Test
    void shouldRejectAssignWithInvalidCategory() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("NAO_EXISTE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"));

        verifyNoInteractions(ticketRepository);
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoSupportRecord() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody("OTHER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldCreateMessageOnAssignedTicket() throws Exception {
        Ticket ticket = ticket(support.getId(), TicketStatus.IN_PROGRESS);
        ZonedDateTime previousUpdate = ticket.getUpdatedAt();
        UUID messageId = UUID.randomUUID();

        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(messageId);
            return message;
        });

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("  Vamos verificar o inversor hoje.  ")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/support/tickets/" + ticketId + "/messages/" + messageId))
                .andExpect(jsonPath("$.senderUserId").value(userId.toString()))
                .andExpect(jsonPath("$.role").value("SUPPORT"))
                .andExpect(jsonPath("$.content").value("Vamos verificar o inversor hoje."));

        ArgumentCaptor<Ticket> savedTicket = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(savedTicket.capture());
        assertNotNull(savedTicket.getValue().getUpdatedAt());
        assertEquals(true, !savedTicket.getValue().getUpdatedAt().isBefore(previousUpdate));
    }

    @Test
    void shouldRejectMessageOnUnassignedTicket() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket(null, TicketStatus.PENDING)));

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Olá")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_ASSIGNED"));

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldRejectMessageOnTicketOfAnotherSupport() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket(UUID.randomUUID(), TicketStatus.IN_PROGRESS)));

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Olá")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_ASSIGNED"));

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldReturnNotFoundWhenMessagingMissingTicket() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Olá")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_FOUND"));
    }

    @Test
    void shouldRejectMessageOnClosedTicket() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket(support.getId(), TicketStatus.RESOLVED)));

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Olá")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_CLOSED"));

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldRejectBlankMessage() throws Exception {
        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("   ")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.content").exists());

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldRejectMessageLongerThan150Characters() throws Exception {
        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("a".repeat(151))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldAccept150CharactersIgnoringSurroundingSpaces() throws Exception {
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findById(ticketId))
                .thenReturn(Optional.of(ticket(support.getId(), TicketStatus.IN_PROGRESS)));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("   " + "a".repeat(150) + "   ")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("a".repeat(150)));
    }
}
