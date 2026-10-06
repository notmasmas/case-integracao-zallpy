package com.branch_master.ecovolt360.messages.domain.repository;

import com.branch_master.ecovolt360.messages.domain.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByTicketId(UUID ticketId);
}
