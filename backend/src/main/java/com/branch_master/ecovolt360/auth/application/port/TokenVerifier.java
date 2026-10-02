package com.branch_master.ecovolt360.auth.application.port;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;

public interface TokenVerifier {
    AuthenticatedUser verify(String token);
}
