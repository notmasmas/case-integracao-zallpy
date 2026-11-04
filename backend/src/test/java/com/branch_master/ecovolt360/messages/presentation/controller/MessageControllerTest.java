package com.branch_master.ecovolt360.messages.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import com.branch_master.ecovolt360.messages.application.dto.MessageBodyDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessagePageDTO;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.messages.presentation.exception.MessageExceptionHandler;
import com.branch_master.ecovolt360.ticket.application.exception.TicketClosedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TICKET_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID MESSAGE_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

    @Mock
    private MessageService messageService;

    @Mock
    private TicketService ticketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MessageController(messageService, ticketService))
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new MessageExceptionHandler(), new AccessExceptionHandler())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(USER_ID, Role.CUSTOMER),
                        null,
                        List.of()
                )
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldListCustomerMessages() throws Exception {
        when(ticketService.listCustomerMessages(USER_ID, TICKET_ID, 0)).thenReturn(
                new MessagePageDTO(List.of(), 0, 0, 0)
        );

        mockMvc.perform(get("/tickets/{ticketId}/messages", TICKET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalMessages").value(0));
    }

    @Test
    void shouldCreateMessage() throws Exception {
        when(messageService.processMessage(eq(USER_ID), eq(Role.CUSTOMER), eq(TICKET_ID), any(MessageBodyDTO.class)))
                .thenReturn(new MessageDTO(
                        MESSAGE_ID,
                        USER_ID,
                        Role.CUSTOMER,
                        "Inversor voltou a comunicar",
                        ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
                ));

        mockMvc.perform(post("/tickets/{ticketId}/messages", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Inversor voltou a comunicar"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/tickets/" + TICKET_ID + "/messages/" + MESSAGE_ID
                ))
                .andExpect(jsonPath("$.id").value(MESSAGE_ID.toString()))
                .andExpect(jsonPath("$.content").value("Inversor voltou a comunicar"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void shouldReturnNotFoundWhenTicketDoesNotExist() throws Exception {
        when(ticketService.listCustomerMessages(USER_ID, TICKET_ID, 0)).thenThrow(new TicketNotFoundException());

        mockMvc.perform(get("/tickets/{ticketId}/messages", TICKET_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_FOUND"));
    }

    @Test
    void shouldReturnConflictWhenTicketIsClosed() throws Exception {
        when(messageService.processMessage(eq(USER_ID), eq(Role.CUSTOMER), eq(TICKET_ID), any(MessageBodyDTO.class)))
                .thenThrow(new TicketClosedException());

        mockMvc.perform(post("/tickets/{ticketId}/messages", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Oi"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_CLOSED"))
                .andExpect(jsonPath("$.message").value("Este chamado não aceita novas mensagens."));
    }
}
