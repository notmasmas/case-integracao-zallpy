package com.branch_master.ecovolt360.support.infrastructure.persistence;

import com.branch_master.ecovolt360.support.domain.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSupportRepository
        extends JpaRepository<Support, UUID> {

    Optional<Support> findByUserId(UUID userId);
}
