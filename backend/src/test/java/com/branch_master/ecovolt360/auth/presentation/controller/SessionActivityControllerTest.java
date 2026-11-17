package com.branch_master.ecovolt360.auth.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.service.SessionActivityService;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessionActivityControllerTest {

    private static final Duration LIMIT = Duration.ofMinutes(15);
    private static final Instant NOW = Instant.parse("2026-11-17T12:00:00Z");

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        SessionActivityService service =
                new SessionActivityService(userRepository, LIMIT, Clock.fixed(NOW, ZoneOffset.UTC));

        mockMvc = MockMvcBuilders
                .standaloneSetup(new SessionActivityController(service))
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new AccessExceptionHandler())
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(UUID id, Role role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new AuthenticatedUser(id, role), null, List.of()));
    }

    private User activeUser(UUID id, Instant lastActivity) {
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(false);
        when(user.getLastActivityAt()).thenReturn(lastActivity);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void shouldRegisterActivityForValidSession() throws Exception {
        authenticateAs(userId, Role.CUSTOMER);
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/session/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.lastActivityAt").exists())
                .andExpect(jsonPath("$.remainingSeconds").value(900))
                .andExpect(jsonPath("$.inactivityLimitSeconds").value(900));

        verify(userRepository).registerActivity(userId, NOW);
    }

    @Test
    void shouldReturnActiveStatusWhenLastActivityIsUnderTheLimit() throws Exception {
        authenticateAs(userId, Role.SUPPORT);
        activeUser(userId, NOW.minus(Duration.ofMinutes(10)));

        mockMvc.perform(get("/session/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.remainingSeconds").value(300));

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldReturnSessionExpiredWhenQueryingAfterTheLimit() throws Exception {
        authenticateAs(userId, Role.CUSTOMER);
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/session/activity"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
    }

    @Test
    void shouldNotRegisterActivityForExpiredSession() throws Exception {
        authenticateAs(userId, Role.CUSTOMER);
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/session/activity"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldRejectRequestsWithoutAuthenticatedSession() throws Exception {
        mockMvc.perform(post("/session/activity"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        mockMvc.perform(get("/session/activity"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verify(userRepository, never()).registerActivity(any(), any());
    }

    @Test
    void shouldIgnoreUserIdentifiersSentByTheClient() throws Exception {
        UUID otherUserId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        authenticateAs(userId, Role.CUSTOMER);
        User user = mock(User.class);
        when(user.isIdleFor(LIMIT, NOW)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/session/activity")
                        .param("userId", otherUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"" + otherUserId + "\"}"))
                .andExpect(status().isOk());

        verify(userRepository).registerActivity(userId, NOW);
        verify(userRepository, never()).registerActivity(otherUserId, NOW);
        verify(userRepository, never()).findById(otherUserId);
    }
}
