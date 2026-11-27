package com.branch_master.ecovolt360.user.domain.repository;

import com.branch_master.ecovolt360.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByFiscalId(String fiscalId);

    boolean existsByEmailAndIdNot(String email, UUID id);

    User save(User user);
}
