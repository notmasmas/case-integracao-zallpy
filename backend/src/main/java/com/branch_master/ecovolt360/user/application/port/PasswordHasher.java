package com.branch_master.ecovolt360.user.application.port;

public interface PasswordHasher {
    String hash(String rawPassword);
}
