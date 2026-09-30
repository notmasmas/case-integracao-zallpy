package com.branch_master.ecovolt360.ticket.infrastructure.config;

import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfiguration {

    @Bean
    public TicketService ticketService(TicketRepository ticketRepository) {
        return new TicketService(ticketRepository);
    }
}
