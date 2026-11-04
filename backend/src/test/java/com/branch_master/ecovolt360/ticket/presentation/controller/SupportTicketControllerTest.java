package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyClosedException;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import com.branch_master.ecovolt360.ticket.presentation.exception.TicketExceptionHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SupportTicketControllerTest {

    private static final UUID USER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final UUID TICKET_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Mock
    private TicketService ticketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SupportTicketController(ticketService))
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new TicketExceptionHandler(), new AccessExceptionHandler())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(USER_ID, Role.SUPPORT),
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
    void shouldListAssignedSupportTickets() throws Exception {
        when(ticketService.listSupportTickets(USER_ID)).thenReturn(List.of(summary("Meu chamado")));

        mockMvc.perform(get("/support/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Meu chamado"));
    }

    @Test
    void shouldListAllTickets() throws Exception {
        when(ticketService.listAllTickets()).thenReturn(List.of(summary("Todos")));

        mockMvc.perform(get("/support/tickets/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Todos"));
    }

    @Test
    void shouldUpdateTicketStatus() throws Exception {
        when(ticketService.updateStatus(eq(USER_ID), eq(TICKET_ID), any(TicketBodyStatusDTO.class)))
                .thenReturn(new TicketDetailsDTO(
                        TICKET_ID,
                        "Meu chamado",
                        "Descricao",
                        null,
                        TicketStatus.IN_PROGRESS,
                        "Ana Suporte",
                        List.of(),
                        0,
                        0,
                        0,
                        null,
                        null,
                        false,
                        ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                        ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
                ));

        mockMvc.perform(patch("/support/tickets/{ticketId}/status", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturnConflictWhenClosedTicketCannotChangeStatus() throws Exception {
        when(ticketService.updateStatus(eq(USER_ID), eq(TICKET_ID), any(TicketBodyStatusDTO.class)))
                .thenThrow(new TicketAlreadyClosedException());

        mockMvc.perform(patch("/support/tickets/{ticketId}/status", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_ALREADY_CLOSED"))
                .andExpect(jsonPath("$.message")
                        .value("Chamado encerrado não pode ter o status alterado."));
    }

    private static TicketSummaryDTO summary(String title) {
        return new TicketSummaryDTO(
                TICKET_ID,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                title,
                "Descricao",
                null,
                TicketStatus.PENDING,
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                "Ana Suporte",
                null,
                null,
                false
        );
    }
}
