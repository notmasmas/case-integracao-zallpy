package com.branch_master.ecovolt360.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
