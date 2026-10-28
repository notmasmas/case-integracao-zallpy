package com.branch_master.ecovolt360.ticket.domain.repository;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID ticketId);
    List<Ticket> findAll();
    Optional<Ticket> findByIdAndCustomerId(UUID ticketId, UUID customerId);
    Optional<Ticket> findByIdAndSupportId(UUID ticketId, UUID supportId);
    List<Ticket> findByCustomerId(UUID customerId);
    List<Ticket> findBySupportId(UUID supportId);
    int updateEvaluation(UUID ticketId, UUID customerId, Integer evaluation, String comment, ZonedDateTime updatedAt);
    int updateStatus(UUID ticketId, TicketStatus status, ZonedDateTime updatedAt, UUID supportId);
}
