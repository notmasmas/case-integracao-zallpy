package com.branch_master.ecovolt360.ticket.infrastructure.persistence;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTicketRepository
        extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByIdAndCustomerId(UUID id, UUID customerId);
}
