package com.branch_master.ecovolt360.auth.application.dto;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        Role role
) {
}
