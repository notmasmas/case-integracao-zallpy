package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.infrastructure.config.SecurityConfiguration;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyAssignDTO;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupportTicketController.class)
@Import(SecurityConfiguration.class)
class SupportTicketSecurityTest {

    private static final String SUPPORT_TOKEN = "support-token";
    private static final String CUSTOMER_TOKEN = "customer-token";
    private static final String INVALID_TOKEN = "invalid-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenVerifier tokenVerifier;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private MessageService messageService;

    private final UUID supportUserId = UUID.randomUUID();
    private final UUID ticketId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(tokenVerifier.verify(SUPPORT_TOKEN)).thenReturn(new AuthenticatedUser(supportUserId, Role.SUPPORT));
        when(tokenVerifier.verify(CUSTOMER_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.CUSTOMER));
        when(tokenVerifier.verify(INVALID_TOKEN)).thenThrow(new InvalidTokenException());
    }

    private String assignBody() {
        return """
                { "category": "EQUIPMENT" }
                """;
    }

    private String messageBody() {
        return """
                { "content": "Olá" }
                """;
    }

    @Test
    void shouldReturnUnauthorizedWhenAssigningWithoutToken() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verifyNoInteractions(ticketService);
    }

    @Test
    void shouldReturnUnauthorizedWhenAssigningWithInvalidToken() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + INVALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verifyNoInteractions(ticketService);
    }

    @Test
    void shouldReturnForbiddenWhenCustomerAssignsTicket() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        verifyNoInteractions(ticketService);
    }

    @Test
    void shouldReturnForbiddenWhenCustomerSendsSupportMessage() throws Exception {
        mockMvc.perform(post("/support/tickets/{ticketId}/messages", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CUSTOMER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        verifyNoInteractions(messageService);
    }

    @Test
    void shouldAllowSupportToAssignTicket() throws Exception {
        mockMvc.perform(patch("/support/tickets/{ticketId}/assign", ticketId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + SUPPORT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody()))
                .andExpect(status().isOk());

        verify(ticketService).assignTicket(eq(supportUserId), eq(ticketId), any(TicketBodyAssignDTO.class));
    }
}
