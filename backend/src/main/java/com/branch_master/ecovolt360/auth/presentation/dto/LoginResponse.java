package com.branch_master.ecovolt360.auth.presentation.dto;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String name,
        Role role,
        String accessToken,
        String tokenType
){
    @Override
    public String toString() {
        return "LoginReponse[userId=" + userId + ",role=" + role + "]";
    }
}