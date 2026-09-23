package com.branch_master.ecovolt360.ticket;

import com.branch_master.ecovolt360.ticket.dto.TicketBodyDTO;
import com.branch_master.ecovolt360.ticket.dto.TicketCreationDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id")
    private UUID supportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private UUID projectId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime createdAt;

    public Ticket() {}

    public Ticket(UUID userId, TicketBodyDTO ticketDTO) {
        this.projectId = ticketDTO.projectId();
        this.customerId = userId;
        this.title = ticketDTO.title();
        this.description = ticketDTO.description();
        this.category = null;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

}
