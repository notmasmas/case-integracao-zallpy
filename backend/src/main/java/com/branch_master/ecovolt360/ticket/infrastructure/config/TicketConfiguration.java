package com.branch_master.ecovolt360.ticket.infrastructure.config;

import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import com.branch_master.ecovolt360.ticket.application.service.TicketService;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketConfiguration {

    @Bean
    public TicketService ticketService(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            SupportRepository supportRepository
    ) {
        return new TicketService(
                ticketRepository,
                customerRepository,
                messageRepository,
                userRepository,
                supportRepository
        );
    }
}
