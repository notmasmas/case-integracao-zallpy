package com.branch_master.ecovolt360.user.infrastructure.security;

import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Pbkdf2PasswordHasher implements PasswordHasher {

    private final Pbkdf2PasswordEncoder encoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
