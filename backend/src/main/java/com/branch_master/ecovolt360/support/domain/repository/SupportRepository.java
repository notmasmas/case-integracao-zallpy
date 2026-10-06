package com.branch_master.ecovolt360.support.domain.repository;

import com.branch_master.ecovolt360.support.domain.entity.Support;

import java.util.Optional;
import java.util.UUID;

public interface SupportRepository {
    Optional<Support> findByUserId(UUID userId);
}
