package com.branch_master.ecovolt360.ticket.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id")
    private UUID customerId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "support_id")
    private UUID supportId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "project_id")
    private UUID projectId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private ZonedDateTime createdAt;

    public Ticket(UUID customerId, UUID projectId, String title, String description) {
        this.projectId = projectId;
        this.customerId = customerId;
        this.supportId = null;
        this.title = title;
        this.description = description;
        this.category = null;
        this.status = TicketStatus.PENDING;
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}
