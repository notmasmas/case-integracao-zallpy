package com.branch_master.ecovolt360.ticket.domain.repository;

import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;

public interface TicketRepository {
    Ticket save(Ticket ticket);
}
