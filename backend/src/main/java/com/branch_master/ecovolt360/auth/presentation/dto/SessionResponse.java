package com.branch_master.ecovolt360.auth.presentation.dto;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

public record SessionResponse(Role role) {
}
