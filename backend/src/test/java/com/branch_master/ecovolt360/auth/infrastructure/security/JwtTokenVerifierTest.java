package com.branch_master.ecovolt360.auth.infrastructure.security;

import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenVerifierTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Mock
    private UserRepository userRepository;

    private JwtTokenIssuer tokenIssuer;
    private JwtTokenVerifier tokenVerifier;

    @BeforeEach
    void setUp() {
        tokenIssuer = new JwtTokenIssuer(SECRET, 60);
        tokenVerifier = new JwtTokenVerifier(SECRET, new ObjectMapper(), userRepository);
    }

    @Test
    void shouldRethrowDataAccessFailureWhenLookingUpUser() {
        UUID userId = UUID.randomUUID();
        String token = tokenIssuer.issue(userId, Role.CUSTOMER, 1);
        DataAccessResourceFailureException failure =
                new DataAccessResourceFailureException("connection refused");
        when(userRepository.findById(userId)).thenThrow(failure);

        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> tokenVerifier.verify(token)
        );

        assertSame(failure, thrown);
    }

    @Test
    void shouldRejectTokenWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        String token = tokenIssuer.issue(userId, Role.CUSTOMER, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify(token));
    }

    @Test
    void shouldRejectTokenWhenVersionDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        String token = tokenIssuer.issue(userId, Role.CUSTOMER, 2);
        User user = mock(User.class);
        when(user.getTokenVersion()).thenReturn(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify("a.b.c"));
    }
}
