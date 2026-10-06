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

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "support_id")
    private UUID supportId;

    @Column(name = "project_id")
    private UUID projectId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Integer evaluate;

    @Column(name = "evaluate_comment")
    private String evaluateComment;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    public Ticket(UUID customerId, UUID projectId, String title, String description) {
        this.projectId = projectId;
        this.customerId = customerId;
        this.supportId = null;
        this.title = title;
        this.description = description;
        this.category = null;
        this.status = TicketStatus.PENDING;
        this.evaluate = null;
        this.evaluateComment = null;
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.updatedAt = this.createdAt;
    }
}
