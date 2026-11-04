package com.branch_master.ecovolt360.ticket.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TicketTest {

    @Test
    void shouldOpenTicketAsPendingWithoutEvaluation() {
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID projectId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        Ticket ticket = new Ticket(customerId, projectId, "Painel solar", "Inversor sem comunicacao");

        assertEquals(customerId, ticket.getCustomerId());
        assertEquals(projectId, ticket.getProjectId());
        assertEquals("Painel solar", ticket.getTitle());
        assertEquals("Inversor sem comunicacao", ticket.getDescription());
        assertNull(ticket.getSupportId());
        assertNull(ticket.getCategory());
        assertEquals(TicketStatus.PENDING, ticket.getStatus());
        assertNull(ticket.getEvaluate());
        assertNull(ticket.getEvaluateComment());
        assertFalse(ticket.isEvaluated());
        assertNotNull(ticket.getCreatedAt());
        assertEquals(ZoneId.of("America/Sao_Paulo"), ticket.getCreatedAt().getZone());
        assertSame(ticket.getCreatedAt(), ticket.getUpdatedAt());
    }
}
