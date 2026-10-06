package com.branch_master.ecovolt360.auth.domain.repository;

import com.branch_master.ecovolt360.auth.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
}