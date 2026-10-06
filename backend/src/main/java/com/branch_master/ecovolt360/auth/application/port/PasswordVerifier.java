package com.branch_master.ecovolt360.auth.application.port;

public interface PasswordVerifier {
    boolean matches(String rawPassword, String storedHash);
}