package com.branch_master.ecovolt360.auth.infrastructure.persistence;

import com.branch_master.ecovolt360.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update AuthUser u set u.lastActivityAt = :now where u.id = :id")
    int updateLastActivity(@Param("id") UUID id, @Param("now") Instant now);
}
