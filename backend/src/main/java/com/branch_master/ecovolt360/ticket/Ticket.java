package com.branch_master.ecovolt360.ticket;

import com.branch_master.ecovolt360.ticket.enums.TicketCategory;
import com.branch_master.ecovolt360.ticket.enums.TicketStatus;
import com.branch_master.ecovolt360.ticket.dto.TicketBodyDTO;
import jakarta.persistence.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="tickets")
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

    public Ticket() {}

        public Ticket(UUID userId, TicketBodyDTO ticketDTO) {
        this.projectId = ticketDTO.projectId();
        this.customerId = userId;
        this.supportId = null;
        this.title = ticketDTO.title();
        this.description = ticketDTO.description();
        this.category = null;
        this.status = TicketStatus.PENDING;
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getSupportId() {
        return supportId;
    }

    public void setSupportId(UUID supportId) {
        this.supportId = supportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
