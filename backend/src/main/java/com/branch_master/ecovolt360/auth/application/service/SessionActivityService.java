package com.branch_master.ecovolt360.auth.application.service;

import com.branch_master.ecovolt360.auth.application.dto.SessionActivity;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.exception.SessionExpiredException;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Registra e consulta a atividade da sessão do usuário autenticado.
 * O usuário vem sempre da sessão autenticada, nunca de dados enviados pelo cliente.
 */
public class SessionActivityService {

    private final UserRepository userRepository;
    private final Duration inactivityLimit;
    private final Clock clock;

    public SessionActivityService(UserRepository userRepository, Duration inactivityLimit, Clock clock) {
        this.userRepository = userRepository;
        this.inactivityLimit = inactivityLimit;
        this.clock = clock;
    }

    /** Registra uma nova atividade. Uma sessão já expirada não pode ser reativada. */
    public SessionActivity register(UUID userId) {
        Instant now = clock.instant();
        findActiveUser(userId, now);

        userRepository.registerActivity(userId, now);

        return SessionActivity.of(now, inactivityLimit, now);
    }

    /** Consulta a situação da sessão sem renovar a atividade. */
    public SessionActivity status(UUID userId) {
        Instant now = clock.instant();
        User user = findActiveUser(userId, now);

        return SessionActivity.of(user.getLastActivityAt(), inactivityLimit, now);
    }

    private User findActiveUser(UUID userId, Instant now) {
        User user = userRepository.findById(userId).orElseThrow(InvalidTokenException::new);
        if (user.isIdleFor(inactivityLimit, now)) {
            throw new SessionExpiredException();
        }
        return user;
    }
}
