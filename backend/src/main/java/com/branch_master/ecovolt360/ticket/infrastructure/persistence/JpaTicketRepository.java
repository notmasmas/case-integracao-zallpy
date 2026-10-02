package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.stereotype.Repository;

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
}
