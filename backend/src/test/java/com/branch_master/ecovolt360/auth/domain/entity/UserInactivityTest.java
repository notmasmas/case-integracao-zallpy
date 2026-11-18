package com.branch_master.ecovolt360.auth.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInactivityTest {

    private static final Duration LIMIT = Duration.ofMinutes(15);
    private static final Instant START = Instant.parse("2026-11-17T12:00:00Z");

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void shouldStartWithoutRecordedActivity() {
        assertNull(user.getLastActivityAt());
    }

    @Test
    void shouldBeIdleWhenThereIsNoRecordedActivity() {
        assertTrue(user.isIdleFor(LIMIT, START));
    }

    @Test
    void shouldStayActiveBeforeTheLimit() {
        user.startSession(LIMIT, START);

        assertFalse(user.isIdleFor(LIMIT, START.plus(Duration.ofMinutes(14)).plusSeconds(59)));
    }

    @Test
    void shouldExpireExactlyAtTheLimit() {
        user.startSession(LIMIT, START);

        assertTrue(user.isIdleFor(LIMIT, START.plus(LIMIT)));
    }

    @Test
    void shouldExpireAfterTheLimit() {
        user.startSession(LIMIT, START);

        assertTrue(user.isIdleFor(LIMIT, START.plus(LIMIT).plusSeconds(1)));
    }

    @Test
    void shouldInvalidateOldTokensWhenStartingSessionAfterIdle() {
        int before = user.getTokenVersion();

        user.startSession(LIMIT, START);

        assertEquals(before + 1, user.getTokenVersion());
        assertEquals(START, user.getLastActivityAt());
    }

    @Test
    void shouldKeepTokenVersionWhenStartingSessionWhileStillActive() {
        user.startSession(LIMIT, START);
        int version = user.getTokenVersion();

        user.startSession(LIMIT, START.plus(Duration.ofMinutes(5)));

        assertEquals(version, user.getTokenVersion());
        assertEquals(START.plus(Duration.ofMinutes(5)), user.getLastActivityAt());
    }
}
