package com.branch_master.ecovolt360.auth.application.service;

import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.exception.InvalidCredentialsException;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordVerifier passwordVerifier;

    @Mock
    private TokenIssuer tokenIssuer;

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(userRepository, passwordVerifier, tokenIssuer);
    }

    @Test
    void shouldReturnTokenForValidCredentials() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getName()).thenReturn("Cliente de teste");
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        when(user.getPassword()).thenReturn("stored-hash");
        when(user.getTokenVersion()).thenReturn(0);
        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordVerifier.matches("senhaTeste", "stored-hash")).thenReturn(true);
        when(tokenIssuer.issue(userId, Role.CUSTOMER, 0)).thenReturn("test-token");

        LoginResult result = loginService.login("cliente@example.com", "senhaTeste");

        assertEquals(userId, result.userId());
        assertEquals("Cliente de teste", result.name());
        assertEquals(Role.CUSTOMER, result.role());
        assertEquals("test-token", result.accessToken());
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login("missing@example.com", "senhaTeste")
        );
        verifyNoInteractions(passwordVerifier, tokenIssuer);
    }

    @Test
    void shouldRejectWrongPassword() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("stored-hash");
        when(userRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(user));
        when(passwordVerifier.matches("senhaErrada", "stored-hash")).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login("cliente@example.com", "senhaErrada")
        );
        verifyNoInteractions(tokenIssuer);
    }

    @Test
    void shouldIncrementTokenVersionOnLogout() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        loginService.logout(userId);

        verify(user).incrementTokenVersion();
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectLogoutWhenUserDoesNotExist() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> loginService.logout(userId));
    }
}
