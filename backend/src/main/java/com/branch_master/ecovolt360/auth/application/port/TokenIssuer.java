package com.branch_master.ecovolt360.auth.application.port;

import com.branch_master.ecovolt360.auth.domain.entity.Role;

import java.util.UUID;

public interface TokenIssuer {

    String issue(UUID userId, Role role);
}