package com.branch_master.ecovolt360.auth.application.dto;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

import java.util.UUID;

public record LoginResult(
        UUID userId,
        String name,
        Role role,
        String accessToken
) {
    @Override
    public String toString() {
        return "LoginResult[userId=]" + userId + ", role=" + role + "]";
    }
}