package com.branch_master.ecovolt360.messages.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "sender_user_id")
    private UUID senderUserId;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    public Message(UUID ticketId, UUID senderUserId, String content, ZonedDateTime createdAt) {
        this.ticketId = ticketId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}