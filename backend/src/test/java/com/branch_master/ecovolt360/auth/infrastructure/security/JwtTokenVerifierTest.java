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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldReturnAuthenticatedUserWhenTokenAndVersionMatch() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String token = tokenIssuer.issue(userId, Role.SUPPORT, 3);
        User user = mock(User.class);
        when(user.getTokenVersion()).thenReturn(3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var authenticated = tokenVerifier.verify(token);

        assertEquals(userId, authenticated.userId());
        assertEquals(Role.SUPPORT, authenticated.role());
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtTokenIssuer expiredIssuer = new JwtTokenIssuer(SECRET, 0);
        UUID userId = UUID.randomUUID();
        String token = expiredIssuer.issue(userId, Role.CUSTOMER, 0);

        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify(token));
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {
        UUID userId = UUID.randomUUID();
        String token = tokenIssuer.issue(userId, Role.CUSTOMER, 0);
        String tampered = token.substring(0, token.length() - 2) + "aa";

        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify(tampered));
    }

    @Test
    void shouldRejectTokenWithoutThreeParts() {
        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify("only-two.parts"));
    }

    @Test
    void shouldRejectTokenWithoutVersionClaim() {
        String header = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String payload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"sub\":\"" + UUID.randomUUID()
                        + "\",\"role\":\"CUSTOMER\",\"iat\":1,\"exp\":9999999999}")
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        JwtTokenIssuer signer = new JwtTokenIssuer(SECRET, 60);
        String valid = signer.issue(UUID.randomUUID(), Role.CUSTOMER, 0);
        String signature = valid.substring(valid.lastIndexOf('.') + 1);
        String tokenWithoutVersion = header + "." + payload + "." + signature;

        assertThrows(InvalidTokenException.class, () -> tokenVerifier.verify(tokenWithoutVersion));
    }
}
