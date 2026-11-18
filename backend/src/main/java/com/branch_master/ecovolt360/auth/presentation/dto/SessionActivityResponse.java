package com.branch_master.ecovolt360.auth.presentation.dto;

import com.branch_master.ecovolt360.auth.application.dto.SessionActivity;

import java.time.Instant;

public record SessionActivityResponse(
        String status,
        Instant lastActivityAt,
        Instant expiresAt,
        long remainingSeconds,
        long inactivityLimitSeconds
) {

    public static final String ACTIVE = "ACTIVE";

    public static SessionActivityResponse active(SessionActivity activity) {
        return new SessionActivityResponse(
                ACTIVE,
                activity.lastActivityAt(),
                activity.expiresAt(),
                activity.remainingSeconds(),
                activity.inactivityLimitSeconds()
        );
    }
}
