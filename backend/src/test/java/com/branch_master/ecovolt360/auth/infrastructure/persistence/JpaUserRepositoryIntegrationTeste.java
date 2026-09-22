package com.branch_master.ecovolt360.auth.infrastructure.persistence;

import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
class JpaUserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        String email = "missing- " +  UUID.randomUUID() + "@example.com";

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isEmpty());
    }
}