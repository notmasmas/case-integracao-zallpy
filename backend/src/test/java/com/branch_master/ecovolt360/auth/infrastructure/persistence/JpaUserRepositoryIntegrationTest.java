package com.branch_master.ecovolt360.auth.infrastructure.persistence;

import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
class JpaUserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private PasswordVerifier passwordVerifier;

    @MockitoBean
    private TokenIssuer tokenIssuer;

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        String email = "missing- " +  UUID.randomUUID() + "@example.com";

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void shouldFindExistingUserByEmail() {
        UUID userId = UUID.randomUUID();
        String email = "integration-" + userId + "@example.com";
        String cpf = "00000000000";
        String storedHash = "hash-placeholder-for-persistence-test";

        jdbcTemplate.update("""
            INSERT INTO users (
                id,
                name,
                cpf,
                email,
                password,
                role
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """,
                userId,
                "Usuário de integração",
                cpf,
                email,
                storedHash,
                Role.CUSTOMER.name()
        );

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent(), "O usuário inserido deve ser encontrado.");

        User user = result.orElseThrow();

        assertEquals(userId, user.getId());
        assertEquals("Usuário de integração", user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(storedHash, user.getPassword());
        assertEquals(Role.CUSTOMER, user.getRole());
    }
}