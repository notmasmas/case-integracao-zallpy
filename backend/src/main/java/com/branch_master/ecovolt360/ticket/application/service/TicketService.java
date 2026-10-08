package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.application.dto.MessageDTO;
import com.branch_master.ecovolt360.messages.application.dto.MessagePageDTO;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyEvaluateDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyStatusDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketDetailsDTO;
import com.branch_master.ecovolt360.ticket.application.dto.TicketSummaryDTO;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyClosedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyEvaluatedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotEvaluatedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.domain.entity.Ticket;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
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
    private final SupportRepository supportRepository;

    public TicketService(
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            SupportRepository supportRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.supportRepository = supportRepository;
    }

    public TicketDetailsDTO processTicket(UUID userId, TicketBodyDTO ticketDTO) {
        Customer customer = findCustomer(userId);
        Ticket newTicket = ticketRepository.save(new Ticket(
                customer.getId(),
                ticketDTO.projectId(),
                ticketDTO.title(),
                ticketDTO.description()
        ));
        return toDetails(newTicket, pageMessages(List.of(), 0));
    }

    public List<TicketSummaryDTO> listCustomerTickets(UUID userId) {
        Customer customer = findCustomer(userId);
        return ticketRepository.findByCustomerId(customer.getId()).stream()
                .map(TicketSummaryDTO::new)
                .toList();
    }

    public TicketDetailsDTO getCustomerTicket(UUID userId, UUID ticketId, int page) {
        Customer customer = findCustomer(userId);
        Ticket ticket = findCustomerTicket(ticketId, customer.getId());
        return toDetails(ticket, pageMessages(messageRepository.findByTicketId(ticket.getId()), page));
    }

    public MessagePageDTO listCustomerMessages(UUID userId, UUID ticketId, int page) {
        Customer customer = findCustomer(userId);
        Ticket ticket = findCustomerTicket(ticketId, customer.getId());
        return pageMessages(messageRepository.findByTicketId(ticket.getId()), page);
    }

    @Transactional
    public TicketDetailsDTO evaluateTicket(UUID userId, UUID ticketId, TicketBodyEvaluateDTO ticketBodyEvaluateDTO) {
        Customer customer = findCustomer(userId);
        Ticket current = findCustomerTicket(ticketId, customer.getId());
        if (current.getStatus() != TicketStatus.CLOSED && current.getStatus() != TicketStatus.RESOLVED) {
            throw new TicketNotEvaluatedException();
        }
        if (current.isEvaluated()) {
            throw new TicketAlreadyEvaluatedException();
        }
        int updated = ticketRepository.updateEvaluation(
                ticketId,
                customer.getId(),
                ticketBodyEvaluateDTO.evaluation(),
                ticketBodyEvaluateDTO.comment(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        if (updated == 0) {
            Ticket latest = findCustomerTicket(ticketId, customer.getId());
            if (latest.isEvaluated()) {
                throw new TicketAlreadyEvaluatedException();
            }
            throw new TicketNotEvaluatedException();
        }
        Ticket ticket = findCustomerTicket(ticketId, customer.getId());
        return toDetails(ticket, pageMessages(messageRepository.findByTicketId(ticket.getId()), 0));
    }

    @Transactional
    public TicketDetailsDTO updateStatus(UUID userId, UUID ticketId, TicketBodyStatusDTO ticketBodyStatusDTO) {
        Ticket current = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        if (current.getStatus() == TicketStatus.CLOSED) {
            throw new TicketAlreadyClosedException();
        }
        int updated = ticketRepository.updateStatus(
                ticketId,
                ticketBodyStatusDTO.status(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );
        if (updated == 0) {
            throw new TicketAlreadyClosedException();
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        return toDetails(ticket, pageMessages(messageRepository.findByTicketId(ticket.getId()), 0));
    }

    private MessagePageDTO pageMessages(List<Message> messages, int page) {
        int totalMessages = messages.size();
        int totalPages = totalMessages == 0 ? 0 : (totalMessages + MESSAGE_PAGE_SIZE - 1) / MESSAGE_PAGE_SIZE;
        int end = totalMessages - page * MESSAGE_PAGE_SIZE;
        if (end <= 0) {
            return new MessagePageDTO(List.of(), page, totalPages, totalMessages);
        }
        int start = Math.max(0, end - MESSAGE_PAGE_SIZE);
        return new MessagePageDTO(toMessageDTOs(messages.subList(start, end)), page, totalPages, totalMessages);
    }

    private TicketDetailsDTO toDetails(Ticket ticket, MessagePageDTO messagePage) {
        return new TicketDetailsDTO(
                ticket,
                supportName(ticket),
                messagePage.messages(),
                messagePage.page(),
                messagePage.totalPages(),
                messagePage.totalMessages()
        );
    }

    private String supportName(Ticket ticket) {
        if (ticket.getSupportId() == null) {
            return "";
        }
        return supportRepository.findById(ticket.getSupportId())
                .map(support -> userRepository.findByIdIn(List.of(support.getUserId())))
                .filter(users -> !users.isEmpty())
                .map(users -> users.getFirst().getName())
                .orElse("");
    }

    private Ticket findCustomerTicket(UUID ticketId, UUID customerId) {
        return ticketRepository.findByIdAndCustomerId(ticketId, customerId)
                .orElseThrow(TicketNotFoundException::new);
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
