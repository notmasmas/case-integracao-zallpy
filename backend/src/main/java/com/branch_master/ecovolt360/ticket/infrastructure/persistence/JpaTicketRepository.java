package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.stereotype.Repository;

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
}
