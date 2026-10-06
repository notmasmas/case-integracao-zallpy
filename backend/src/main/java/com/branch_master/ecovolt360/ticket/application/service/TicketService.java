package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;

import java.util.UUID;

public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    public TicketService(TicketRepository ticketRepository, CustomerRepository customerRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
    }

    public TicketDetailsDTO processTicket(UUID userId, TicketBodyDTO ticketDTO) {
        Customer customer = findCustomer(userId);
        Ticket newTicket = ticketRepository.save(new Ticket(
                customer.getId(),
                ticketDTO.projectId(),
                ticketDTO.title(),
                ticketDTO.description()
        ));
        return new TicketDetailsDTO(newTicket);
    }

    public TicketDetailsDTO getCustomerTicket(UUID userId, UUID ticketId) {
        Customer customer = findCustomer(userId);
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customer.getId())
                .orElseThrow(TicketNotFoundException::new);
        return new TicketDetailsDTO(ticket);
    }

    private Customer findCustomer(UUID userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(ForbiddenException::new);
    }
}
