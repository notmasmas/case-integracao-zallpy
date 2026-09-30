package com.branch_master.ecovolt360.user.infrastructure.persistence;

import com.branch_master.ecovolt360.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserAccountRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
