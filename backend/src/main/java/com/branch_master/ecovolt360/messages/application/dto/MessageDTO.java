package com.branch_master.ecovolt360.messages.application.dto;

import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.messages.domain.entity.Message;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MessageDTO(
        UUID id,
        UUID senderUserId,
        Role role,
        String content,
        ZonedDateTime createdAt
) {
    public MessageDTO(Message message, Role role) {
        this(
                message.getId(),
                message.getSenderUserId(),
                role,
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
