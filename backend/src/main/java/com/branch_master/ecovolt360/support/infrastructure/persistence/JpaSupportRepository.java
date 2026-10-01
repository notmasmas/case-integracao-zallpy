package com.branch_master.ecovolt360.support.infrastructure.persistence;

import com.branch_master.ecovolt360.support.domain.entity.Support;
import com.branch_master.ecovolt360.support.domain.repository.SupportRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSupportRepository implements SupportRepository {

    private final SpringDataSupportRepository repository;

    public JpaSupportRepository(SpringDataSupportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Support> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}
