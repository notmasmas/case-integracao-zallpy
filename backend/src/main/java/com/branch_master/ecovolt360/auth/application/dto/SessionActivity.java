package com.branch_master.ecovolt360.auth.application.dto;

import java.time.Duration;
import java.time.Instant;

/** Situação de atividade de uma sessão ativa. */
public record SessionActivity(
        Instant lastActivityAt,
        Instant expiresAt,
        long remainingSeconds,
        long inactivityLimitSeconds
) {

    public static SessionActivity of(Instant lastActivityAt, Duration limit, Instant now) {
        Instant expiresAt = lastActivityAt.plus(limit);
        long remaining = Math.max(0, Duration.between(now, expiresAt).getSeconds());
        return new SessionActivity(lastActivityAt, expiresAt, remaining, limit.getSeconds());
    }
}
