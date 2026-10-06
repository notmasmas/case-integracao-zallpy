package com.branch_master.ecovolt360.messages.infrastructure.persistence;

import com.branch_master.ecovolt360.messages.domain.entity.Message;
import com.branch_master.ecovolt360.messages.domain.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaMessageRepository implements MessageRepository {

    private final SpringDataMessageRepository repository;

    public JpaMessageRepository(SpringDataMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Message save(Message message) {
        return repository.save(message);
    }

    @Override
    public List<Message> findByTicketId(UUID ticketId) {
        return repository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}
