package com.branch_master.ecovolt360.messages.infrastructure.config;

import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.messages.application.service.MessageService;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfiguration {
    @Bean
    public MessageService messageService(
            MessageRepository messageRepository,
            TicketRepository ticketRepository,
            CustomerRepository customerRepository
    ) {
        return new MessageService(messageRepository, ticketRepository, customerRepository);
    }
}