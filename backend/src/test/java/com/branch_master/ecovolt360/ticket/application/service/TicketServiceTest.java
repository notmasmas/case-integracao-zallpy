package com.branch_master.ecovolt360.ticket.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.messages.application.dto.MessagePageDTO;
import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import com.branch_master.ecovolt360.support.domain.entity.Support;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import com.branch_master.ecovolt360.ticket.application.dto.TicketBodyCategoryDTO;
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
import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;
import com.branch_master.ecovolt360.ticket.domain.entity.TicketStatus;
import com.branch_master.ecovolt360.ticket.domain.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SupportRepository supportRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(
                ticketRepository,
                customerRepository,
                messageRepository,
                userRepository,
                supportRepository
        );
    }

    @Test
    void shouldCreatePendingTicketForCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID projectId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        Customer customer = new Customer(userId);
        customer.setId(customerId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setId(ticketId);
            return ticket;
        });

        TicketDetailsDTO result = ticketService.processTicket(
                userId,
                new TicketBodyDTO(projectId, "Painel solar", "Inversor sem comunicacao")
        );

        assertEquals(ticketId, result.id());
        assertEquals("Painel solar", result.title());
        assertEquals("Inversor sem comunicacao", result.description());
        assertEquals(TicketStatus.PENDING, result.status());
        assertNull(result.category());
        assertEquals("", result.supportName());
        assertTrue(result.messages().isEmpty());
        assertEquals(0, result.page());
        assertEquals(0, result.totalPages());
        assertEquals(0, result.totalMessages());
        assertFalse(result.isEvaluated());
        assertNull(result.evaluate());
        assertNull(result.evaluateComment());
    }

    @Test
    void shouldRejectTicketCreationWhenUserIsNotCustomer() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID projectId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
                ForbiddenException.class,
                () -> ticketService.processTicket(
                        userId,
                        new TicketBodyDTO(projectId, "Painel solar", "Inversor sem comunicacao")
                )
        );
    }

    @Test
    void shouldListAllTicketsWithSupportName() {
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID supportUserId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        Ticket ticket = ticketWithSupport("Chamado aberto", supportId);
        Support support = new Support(supportUserId);
        support.setId(supportId);
        User supportUser = mock(User.class);
        when(supportUser.getName()).thenReturn("Ana Suporte");
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(supportRepository.findById(supportId)).thenReturn(Optional.of(support));
        when(userRepository.findByIdIn(List.of(supportUserId))).thenReturn(List.of(supportUser));

        List<TicketSummaryDTO> result = ticketService.listAllTickets();

        assertEquals(1, result.size());
        assertEquals("Chamado aberto", result.getFirst().title());
        assertEquals("Ana Suporte", result.getFirst().supportName());
    }

    @Test
    void shouldListCustomerTickets() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        Customer customer = customer(userId, customerId);
        Ticket ticket = ticketForCustomer(customerId, "Meu chamado");
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByCustomerId(customerId)).thenReturn(List.of(ticket));

        List<TicketSummaryDTO> result = ticketService.listCustomerTickets(userId);

        assertEquals(1, result.size());
        assertEquals("Meu chamado", result.getFirst().title());
        assertEquals("", result.getFirst().supportName());
    }

    @Test
    void shouldListSupportTickets() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        Support support = new Support(userId);
        support.setId(supportId);
        Ticket ticket = ticketWithSupport("Chamado atribuido", supportId);
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findBySupportId(supportId)).thenReturn(List.of(ticket));
        when(supportRepository.findById(supportId)).thenReturn(Optional.of(support));
        when(userRepository.findByIdIn(List.of(userId))).thenReturn(List.of());

        List<TicketSummaryDTO> result = ticketService.listSupportTickets(userId);

        assertEquals(1, result.size());
        assertEquals("Chamado atribuido", result.getFirst().title());
        assertEquals("", result.getFirst().supportName());
    }

    @Test
    void shouldRejectSupportTicketListWhenUserIsNotSupport() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> ticketService.listSupportTickets(userId));
    }

    @Test
    void shouldReturnCustomerTicketDetailsWithNewestMessagesOnFirstPage() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = customer(userId, customerId);
        Ticket ticket = ticketForCustomer(customerId, "Detalhe");
        ticket.setId(ticketId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(ticket));
        when(messageRepository.findByTicketId(ticketId)).thenReturn(fiveMessages(ticketId, userId));
        User sender = mock(User.class);
        when(sender.getId()).thenReturn(userId);
        when(sender.getRole()).thenReturn(Role.CUSTOMER);
        when(userRepository.findByIdIn(List.of(userId))).thenReturn(List.of(sender));

        TicketDetailsDTO result = ticketService.getCustomerTicket(userId, ticketId, 0);

        assertEquals("Detalhe", result.title());
        assertEquals(0, result.page());
        assertEquals(2, result.totalPages());
        assertEquals(5, result.totalMessages());
        assertEquals(4, result.messages().size());
        assertEquals("msg-2", result.messages().getFirst().content());
        assertEquals("msg-5", result.messages().getLast().content());
        assertEquals(Role.CUSTOMER, result.messages().getFirst().role());
    }

    @Test
    void shouldReturnOlderMessagesOnSecondPage() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = customer(userId, customerId);
        Ticket ticket = ticketForCustomer(customerId, "Detalhe");
        ticket.setId(ticketId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(ticket));
        when(messageRepository.findByTicketId(ticketId)).thenReturn(fiveMessages(ticketId, userId));
        User sender = mock(User.class);
        when(sender.getId()).thenReturn(userId);
        when(sender.getRole()).thenReturn(Role.CUSTOMER);
        when(userRepository.findByIdIn(List.of(userId))).thenReturn(List.of(sender));

        MessagePageDTO result = ticketService.listCustomerMessages(userId, ticketId, 1);

        assertEquals(1, result.page());
        assertEquals(2, result.totalPages());
        assertEquals(5, result.totalMessages());
        assertEquals(1, result.messages().size());
        assertEquals("msg-1", result.messages().getFirst().content());
    }

    @Test
    void shouldReturnEmptyMessagePageWhenPageIsPastEnd() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = customer(userId, customerId);
        Ticket ticket = ticketForCustomer(customerId, "Detalhe");
        ticket.setId(ticketId);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(ticket));
        when(messageRepository.findByTicketId(ticketId)).thenReturn(fiveMessages(ticketId, userId));

        MessagePageDTO result = ticketService.listCustomerMessages(userId, ticketId, 2);

        assertEquals(2, result.page());
        assertEquals(2, result.totalPages());
        assertEquals(5, result.totalMessages());
        assertTrue(result.messages().isEmpty());
    }

    @Test
    void shouldRejectUnknownCustomerTicket() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(userId, customerId)));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.getCustomerTicket(userId, ticketId, 0));
    }

    @Test
    void shouldEvaluateClosedTicket() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Customer customer = customer(userId, customerId);
        Ticket current = ticketForCustomer(customerId, "Fechado");
        current.setId(ticketId);
        current.setStatus(TicketStatus.CLOSED);
        Ticket evaluated = ticketForCustomer(customerId, "Fechado");
        evaluated.setId(ticketId);
        evaluated.setStatus(TicketStatus.CLOSED);
        evaluated.setEvaluated(true);
        evaluated.setEvaluate(5);
        evaluated.setEvaluateComment("Excelente");
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId))
                .thenReturn(Optional.of(current), Optional.of(evaluated));
        when(ticketRepository.updateEvaluation(eq(ticketId), eq(customerId), eq(5), eq("Excelente"), any()))
                .thenReturn(1);
        when(messageRepository.findByTicketId(ticketId)).thenReturn(List.of());

        TicketDetailsDTO result = ticketService.evaluateTicket(
                userId,
                ticketId,
                new TicketBodyEvaluateDTO(5, "Excelente")
        );

        assertTrue(result.isEvaluated());
        assertEquals(5, result.evaluate());
        assertEquals("Excelente", result.evaluateComment());
    }

    @Test
    void shouldRejectEvaluationWhenTicketIsStillOpen() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Ticket current = ticketForCustomer(customerId, "Aberto");
        current.setId(ticketId);
        current.setStatus(TicketStatus.IN_PROGRESS);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(userId, customerId)));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(current));

        assertThrows(
                TicketNotEvaluatedException.class,
                () -> ticketService.evaluateTicket(userId, ticketId, new TicketBodyEvaluateDTO(4, "ok"))
        );
    }

    @Test
    void shouldRejectEvaluationWhenTicketWasAlreadyEvaluated() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Ticket current = ticketForCustomer(customerId, "Resolvido");
        current.setId(ticketId);
        current.setStatus(TicketStatus.RESOLVED);
        current.setEvaluated(true);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(userId, customerId)));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId)).thenReturn(Optional.of(current));

        assertThrows(
                TicketAlreadyEvaluatedException.class,
                () -> ticketService.evaluateTicket(userId, ticketId, new TicketBodyEvaluateDTO(3, "tarde"))
        );
    }

    @Test
    void shouldRejectEvaluationWhenConcurrentUpdateAlreadyEvaluated() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Ticket current = ticketForCustomer(customerId, "Fechado");
        current.setId(ticketId);
        current.setStatus(TicketStatus.CLOSED);
        Ticket latest = ticketForCustomer(customerId, "Fechado");
        latest.setId(ticketId);
        latest.setStatus(TicketStatus.CLOSED);
        latest.setEvaluated(true);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(userId, customerId)));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId))
                .thenReturn(Optional.of(current), Optional.of(latest));
        when(ticketRepository.updateEvaluation(eq(ticketId), eq(customerId), eq(2), eq("ok"), any()))
                .thenReturn(0);

        assertThrows(
                TicketAlreadyEvaluatedException.class,
                () -> ticketService.evaluateTicket(userId, ticketId, new TicketBodyEvaluateDTO(2, "ok"))
        );
    }

    @Test
    void shouldRejectEvaluationWhenConcurrentUpdateLeavesTicketNotEvaluable() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID customerId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Ticket current = ticketForCustomer(customerId, "Resolvido");
        current.setId(ticketId);
        current.setStatus(TicketStatus.RESOLVED);
        Ticket latest = ticketForCustomer(customerId, "Resolvido");
        latest.setId(ticketId);
        latest.setStatus(TicketStatus.RESOLVED);
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(userId, customerId)));
        when(ticketRepository.findByIdAndCustomerId(ticketId, customerId))
                .thenReturn(Optional.of(current), Optional.of(latest));
        when(ticketRepository.updateEvaluation(eq(ticketId), eq(customerId), eq(1), eq("ok"), any()))
                .thenReturn(0);

        assertThrows(
                TicketNotEvaluatedException.class,
                () -> ticketService.evaluateTicket(userId, ticketId, new TicketBodyEvaluateDTO(1, "ok"))
        );
    }

    @Test
    void shouldUpdateTicketStatusForSupport() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Support support = new Support(userId);
        support.setId(supportId);
        Ticket current = ticketWithSupport("Em andamento", supportId);
        current.setId(ticketId);
        Ticket updated = ticketWithSupport("Em andamento", supportId);
        updated.setId(ticketId);
        updated.setStatus(TicketStatus.IN_PROGRESS);
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findByIdAndSupportId(ticketId, supportId))
                .thenReturn(Optional.of(current), Optional.of(updated));
        when(ticketRepository.updateStatus(eq(ticketId), eq(TicketStatus.IN_PROGRESS), any(), eq(supportId)))
                .thenReturn(1);
        when(messageRepository.findByTicketId(ticketId)).thenReturn(List.of());
        when(supportRepository.findById(supportId)).thenReturn(Optional.of(support));
        when(userRepository.findByIdIn(List.of(userId))).thenReturn(List.of());

        TicketDetailsDTO result = ticketService.updateStatus(
                userId,
                ticketId,
                new TicketBodyStatusDTO(TicketStatus.IN_PROGRESS)
        );

        assertEquals(TicketStatus.IN_PROGRESS, result.status());
    }

    @Test
    void shouldRejectStatusUpdateWhenTicketIsAlreadyClosed() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Support support = new Support(userId);
        support.setId(supportId);
        Ticket current = ticketWithSupport("Encerrado", supportId);
        current.setId(ticketId);
        current.setStatus(TicketStatus.CLOSED);
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findByIdAndSupportId(ticketId, supportId)).thenReturn(Optional.of(current));

        assertThrows(
                TicketAlreadyClosedException.class,
                () -> ticketService.updateStatus(userId, ticketId, new TicketBodyStatusDTO(TicketStatus.IN_PROGRESS))
        );
    }

    @Test
    void shouldRejectStatusUpdateWhenConcurrentCloseWins() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Support support = new Support(userId);
        support.setId(supportId);
        Ticket current = ticketWithSupport("Aberto", supportId);
        current.setId(ticketId);
        Ticket latest = ticketWithSupport("Aberto", supportId);
        latest.setId(ticketId);
        latest.setStatus(TicketStatus.CLOSED);
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findByIdAndSupportId(ticketId, supportId))
                .thenReturn(Optional.of(current), Optional.of(latest));
        when(ticketRepository.updateStatus(eq(ticketId), eq(TicketStatus.IN_PROGRESS), any(), eq(supportId)))
                .thenReturn(0);

        assertThrows(
                TicketAlreadyClosedException.class,
                () -> ticketService.updateStatus(userId, ticketId, new TicketBodyStatusDTO(TicketStatus.IN_PROGRESS))
        );
    }

    @Test
    void shouldRejectStatusUpdateWhenConcurrentUpdateFindsNoTicket() {
        UUID userId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID supportId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Support support = new Support(userId);
        support.setId(supportId);
        Ticket current = ticketWithSupport("Aberto", supportId);
        current.setId(ticketId);
        Ticket latest = ticketWithSupport("Aberto", supportId);
        latest.setId(ticketId);
        latest.setStatus(TicketStatus.IN_PROGRESS);
        when(supportRepository.findByUserId(userId)).thenReturn(Optional.of(support));
        when(ticketRepository.findByIdAndSupportId(ticketId, supportId))
                .thenReturn(Optional.of(current), Optional.of(latest));
        when(ticketRepository.updateStatus(eq(ticketId), eq(TicketStatus.RESOLVED), any(), eq(supportId)))
                .thenReturn(0);

        assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.updateStatus(userId, ticketId, new TicketBodyStatusDTO(TicketStatus.RESOLVED))
        );
    }

    @Test
    void shouldUpdateTicketCategory() {
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Ticket ticket = new Ticket(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Categoria",
                "Atualizar categoria"
        );
        ticket.setId(ticketId);
        ticket.setCategory(TicketCategory.EQUIPMENT);
        when(ticketRepository.updateCategory(eq(ticketId), eq(TicketCategory.EQUIPMENT), any())).thenReturn(1);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(messageRepository.findByTicketId(ticketId)).thenReturn(List.of());

        TicketDetailsDTO result = ticketService.updateCategory(
                ticketId,
                new TicketBodyCategoryDTO(TicketCategory.EQUIPMENT)
        );

        assertEquals(TicketCategory.EQUIPMENT, result.category());
    }

    @Test
    void shouldRejectCategoryUpdateWhenTicketDoesNotExist() {
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(ticketRepository.updateCategory(eq(ticketId), eq(TicketCategory.PAYMENT), any())).thenReturn(0);

        assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.updateCategory(ticketId, new TicketBodyCategoryDTO(TicketCategory.PAYMENT))
        );
    }

    @Test
    void shouldRejectCategoryUpdateWhenTicketDisappearsAfterUpdate() {
        UUID ticketId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(ticketRepository.updateCategory(eq(ticketId), eq(TicketCategory.ACCESS), any())).thenReturn(1);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.updateCategory(ticketId, new TicketBodyCategoryDTO(TicketCategory.ACCESS))
        );
    }

    private static Customer customer(UUID userId, UUID customerId) {
        Customer customer = new Customer(userId);
        customer.setId(customerId);
        return customer;
    }

    private static Ticket ticketForCustomer(UUID customerId, String title) {
        return new Ticket(
                customerId,
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                title,
                "Descricao do chamado"
        );
    }

    private static Ticket ticketWithSupport(String title, UUID supportId) {
        Ticket ticket = ticketForCustomer(UUID.fromString("22222222-2222-2222-2222-222222222222"), title);
        ticket.setSupportId(supportId);
        return ticket;
    }

    private static List<Message> fiveMessages(UUID ticketId, UUID senderUserId) {
        ZonedDateTime createdAt = ZonedDateTime.of(2026, 11, 4, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"));
        return List.of(
                message(ticketId, senderUserId, "msg-1", createdAt),
                message(ticketId, senderUserId, "msg-2", createdAt.plusMinutes(1)),
                message(ticketId, senderUserId, "msg-3", createdAt.plusMinutes(2)),
                message(ticketId, senderUserId, "msg-4", createdAt.plusMinutes(3)),
                message(ticketId, senderUserId, "msg-5", createdAt.plusMinutes(4))
        );
    }

    private static Message message(UUID ticketId, UUID senderUserId, String content, ZonedDateTime createdAt) {
        Message message = new Message(ticketId, senderUserId, content, createdAt);
        message.setId(UUID.randomUUID());
        message.setCreatedAt(createdAt);
        return message;
    }
}
