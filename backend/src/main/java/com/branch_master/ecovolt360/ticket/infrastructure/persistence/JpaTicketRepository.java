package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTicketRepository implements TicketRepository {

    private final SpringDataTicketRepository repository;

    public JpaTicketRepository(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return repository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(UUID ticketId) {
        return repository.findById(ticketId);
    }

    @Override
    public Optional<Ticket> findByIdAndCustomerId(UUID ticketId, UUID customerId) {
        return repository.findByIdAndCustomerId(ticketId, customerId);
    }

    @Override
    public List<Ticket> findByCustomerId(UUID customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public int updateEvaluation(UUID ticketId, UUID customerId, Integer evaluation, String comment, ZonedDateTime updatedAt) {
        return repository.updateEvaluationAndComment(ticketId, customerId, evaluation, comment, updatedAt);
    }

    @Override
    public int updateStatus(UUID ticketId, TicketStatus status, ZonedDateTime updatedAt) {
        return repository.updateStatus(ticketId, status, updatedAt);
    }
}
