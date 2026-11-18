package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyCategoryDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyEvaluateDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyEvaluatedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerTicketControllerTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TICKET_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID PROJECT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private TicketService ticketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CustomerTicketController(ticketService))
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new TicketExceptionHandler(), new AccessExceptionHandler())
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
    void shouldCreateTicket() throws Exception {
        when(ticketService.processTicket(eq(USER_ID), any(TicketBodyDTO.class))).thenReturn(details());

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": "33333333-3333-3333-3333-333333333333",
                                  "title": "Painel solar",
                                  "description": "Inversor sem comunicacao"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tickets/" + TICKET_ID))
                .andExpect(jsonPath("$.id").value(TICKET_ID.toString()))
                .andExpect(jsonPath("$.title").value("Painel solar"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldListCustomerTickets() throws Exception {
        when(ticketService.listCustomerTickets(USER_ID)).thenReturn(List.of(
                new TicketSummaryDTO(
                        TICKET_ID,
                        USER_ID,
                        null,
                        PROJECT_ID,
                        "Painel solar",
                        "Inversor sem comunicacao",
                        null,
                        TicketStatus.PENDING,
                        ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                        "",
                        null,
                        null,
                        false
                )
        ));

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TICKET_ID.toString()))
                .andExpect(jsonPath("$[0].title").value("Painel solar"));
    }

    @Test
    void shouldGetCustomerTicket() throws Exception {
        when(ticketService.getCustomerTicket(USER_ID, TICKET_ID, 0)).thenReturn(details());

        mockMvc.perform(get("/tickets/{id}", TICKET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TICKET_ID.toString()))
                .andExpect(jsonPath("$.totalMessages").value(0));
    }

    @Test
    void shouldEvaluateTicket() throws Exception {
        TicketDetailsDTO evaluated = new TicketDetailsDTO(
                TICKET_ID,
                "Painel solar",
                "Inversor sem comunicacao",
                null,
                TicketStatus.CLOSED,
                "",
                List.of(),
                0,
                0,
                0,
                5,
                "Excelente",
                true,
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                ZonedDateTime.of(2026, 11, 4, 11, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        );
        when(ticketService.evaluateTicket(eq(USER_ID), eq(TICKET_ID), any(TicketBodyEvaluateDTO.class)))
                .thenReturn(evaluated);

        mockMvc.perform(patch("/tickets/{id}/evaluation", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "evaluation": 5,
                                  "comment": "Excelente"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluate").value(5))
                .andExpect(jsonPath("$.evaluateComment").value("Excelente"));

        verify(ticketService).evaluateTicket(eq(USER_ID), eq(TICKET_ID), any(TicketBodyEvaluateDTO.class));
    }

    @Test
    void shouldUpdateStatus() throws Exception {
        when(ticketService.updateStatus(eq(USER_ID), eq(TICKET_ID), any(TicketBodyStatusDTO.class)))
                .thenReturn(details());

        mockMvc.perform(patch("/tickets/{id}/status", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TICKET_ID.toString()));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        TicketDetailsDTO categorized = new TicketDetailsDTO(
                TICKET_ID,
                "Painel solar",
                "Inversor sem comunicacao",
                TicketCategory.EQUIPMENT,
                TicketStatus.PENDING,
                "",
                List.of(),
                0,
                0,
                0,
                null,
                null,
                false,
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        );
        when(ticketService.updateCategory(eq(TICKET_ID), any(TicketBodyCategoryDTO.class))).thenReturn(categorized);

        mockMvc.perform(patch("/tickets/{id}/category", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "EQUIPMENT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("EQUIPMENT"));
    }

    @Test
    void shouldReturnNotFoundWhenTicketDoesNotExist() throws Exception {
        when(ticketService.getCustomerTicket(USER_ID, TICKET_ID, 0)).thenThrow(new TicketNotFoundException());

        mockMvc.perform(get("/tickets/{id}", TICKET_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TICKET_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Chamado não encontrado."));
    }

    @Test
    void shouldReturnConflictWhenTicketWasAlreadyEvaluated() throws Exception {
        when(ticketService.evaluateTicket(eq(USER_ID), eq(TICKET_ID), any(TicketBodyEvaluateDTO.class)))
                .thenThrow(new TicketAlreadyEvaluatedException());

        mockMvc.perform(patch("/tickets/{id}/evaluation", TICKET_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "evaluation": 4,
                                  "comment": "ok"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TICKET_ALREADY_EVALUATED"));
    }

    private static TicketDetailsDTO details() {
        return new TicketDetailsDTO(
                TICKET_ID,
                "Painel solar",
                "Inversor sem comunicacao",
                null,
                TicketStatus.PENDING,
                "",
                List.of(),
                0,
                0,
                0,
                null,
                null,
                false,
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo")),
                ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        );
    }
}
