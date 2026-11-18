package com.branch_master.ecovolt360.auth.application.service;

import com.branch_master.ecovolt360.auth.application.dto.SessionActivity;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.exception.SessionExpiredException;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionActivityServiceTest {

    private static final Duration LIMIT = Duration.ofMinutes(15);
    private static final Instant NOW = Instant.parse("2026-11-17T12:00:00Z");

    @Mock
    private UserRepository userRepository;

    private SessionActivityService service;
    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        service = new SessionActivityService(userRepository, LIMIT, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private User userWith(boolean idle, Instant lastActivity) {
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(idle);
        if (lastActivity != null) {
            when(user.getLastActivityAt()).thenReturn(lastActivity);
        }
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void shouldRegisterActivityForValidSession() {
        userWith(false, null);

        SessionActivity activity = service.register(userId);

        verify(userRepository).registerActivity(userId, NOW);
        assertEquals(NOW, activity.lastActivityAt());
        assertEquals(NOW.plus(LIMIT), activity.expiresAt());
        assertEquals(900, activity.remainingSeconds());
        assertEquals(900, activity.inactivityLimitSeconds());
    }

    @Test
    void shouldNotRegisterActivityForExpiredSession() {
        userWith(true, null);

        assertThrows(SessionExpiredException.class, () -> service.register(userId));

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldRejectRegisterWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> service.register(userId));

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldReportActiveSessionWithRemainingTime() {
        Instant lastActivity = NOW.minus(Duration.ofMinutes(10));
        userWith(false, lastActivity);

        SessionActivity activity = service.status(userId);

        assertEquals(lastActivity, activity.lastActivityAt());
        assertEquals(lastActivity.plus(LIMIT), activity.expiresAt());
        assertEquals(300, activity.remainingSeconds());
    }

    @Test
    void shouldNotRenewActivityWhenQueryingStatus() {
        userWith(false, NOW.minus(Duration.ofMinutes(1)));

        service.status(userId);

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldReportExpiredSessionWhenIdleForTooLong() {
        userWith(true, null);

        assertThrows(SessionExpiredException.class, () -> service.status(userId));
    }

    @Test
    void shouldRejectStatusWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> service.status(userId));
    }
}
