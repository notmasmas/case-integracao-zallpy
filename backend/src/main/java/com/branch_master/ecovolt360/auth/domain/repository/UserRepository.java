package com.branch_master.ecovolt360.auth.domain.repository;

import com.branch_master.ecovolt360.auth.domain.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    List<User> findByIdIn(Collection<UUID> ids);
}