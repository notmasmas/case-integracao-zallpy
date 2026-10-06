package com.branch_master.ecovolt360.ticket.domain.repository;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID ticketId);
    Optional<Ticket> findByIdAndCustomerId(UUID ticketId, UUID customerId);
}
