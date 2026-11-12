package com.branch_master.ecovolt360.auth.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void shouldStartWithTokenVersionZero() {
        User user = new User();

        assertEquals(0, user.getTokenVersion());
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    void shouldIncrementTokenVersionOnLogout() {
        User user = new User();

        user.incrementTokenVersion();
        user.incrementTokenVersion();

        assertEquals(2, user.getTokenVersion());
    }
}
