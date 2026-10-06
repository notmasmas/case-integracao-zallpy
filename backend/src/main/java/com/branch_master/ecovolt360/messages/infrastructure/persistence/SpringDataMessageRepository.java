package com.branch_master.ecovolt360.messages.infrastructure.persistence;

import com.branch_master.ecovolt360.messages.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
