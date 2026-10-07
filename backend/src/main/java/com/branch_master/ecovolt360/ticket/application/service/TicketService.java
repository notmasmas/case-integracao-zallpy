package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyEvaluateDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import jakarta.transaction.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketService {

    private static final int MESSAGE_PAGE_SIZE = 4;

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            MessageRepository messageRepository,
            UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public TicketDetailsDTO processTicket(UUID userId, TicketBodyDTO ticketDTO) {
        Customer customer = findCustomer(userId);
        Ticket newTicket = ticketRepository.save(new Ticket(
                customer.getId(),
                ticketDTO.projectId(),
                ticketDTO.title(),
                ticketDTO.description()
        ));
        return new TicketDetailsDTO(newTicket, List.of());
    }

    public List<TicketSummaryDTO> listCustomerTickets(UUID userId) {
        Customer customer = findCustomer(userId);
        return ticketRepository.findByCustomerId(customer.getId()).stream()
                .map(TicketSummaryDTO::new)
                .toList();
    }

    public TicketDetailsDTO getCustomerTicket(UUID userId, UUID ticketId, int page) {
        Customer customer = findCustomer(userId);
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customer.getId())
                .orElseThrow(TicketNotFoundException::new);
        List<Message> messages = messageRepository.findByTicketId(ticket.getId());
        int end = messages.size() - page * MESSAGE_PAGE_SIZE;
        if (end <= 0) {
            return new TicketDetailsDTO(ticket, List.of(), false);
        }
        int start = Math.max(0, end - MESSAGE_PAGE_SIZE);
        return new TicketDetailsDTO(ticket, toMessageDTOs(messages.subList(start, end)), start > 0);
    }

    @Transactional
    public TicketDetailsDTO evaluateTicket(UUID userId, UUID ticketId, TicketBodyEvaluateDTO ticketBodyEvaluateDTO) {
        Customer customer = findCustomer(userId);
        int updated = ticketRepository.updateEvaluation(
                ticketId,
                customer.getId(),
                ticketBodyEvaluateDTO.evaluation(),
                ticketBodyEvaluateDTO.comment(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        if (updated == 0) {
            throw new TicketNotFoundException();
        }
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customer.getId())
                .orElseThrow(TicketNotFoundException::new);
        List<Message> messages = messageRepository.findByTicketId(ticket.getId());
        return new TicketDetailsDTO(ticket, toMessageDTOs(messages));
    } 

    private List<MessageDTO> toMessageDTOs(List<Message> messages) {
        if (messages.isEmpty()) {
            return List.of();
        }

        List<UUID> senderIds = messages.stream()
                .map(Message::getSenderUserId)
                .distinct()
                .toList();
        Map<UUID, Role> rolesByUserId = userRepository.findByIdIn(senderIds).stream()
                .collect(Collectors.toMap(User::getId, User::getRole));

        return messages.stream()
                .map(message -> new MessageDTO(message, rolesByUserId.get(message.getSenderUserId())))
                .toList();
    }

    private Customer findCustomer(UUID userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(ForbiddenException::new);
    }
}
