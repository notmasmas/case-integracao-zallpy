package com.branch_master.ecovolt360.auth.application.service;

import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.exception.InvalidCredentialsException;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class LoginService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final TokenIssuer tokenIssuer;

    public LoginService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            TokenIssuer tokenIssuer
    ) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.tokenIssuer = tokenIssuer;
    }

    public LoginResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches =
                passwordVerifier.matches(password, user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token = tokenIssuer.issue(
                user.getId(),
                user.getRole(),
                user.getTokenVersion()
        );

        return new LoginResult(
                user.getId(),
                user.getName(),
                user.getRole(),
                token
        );
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);
        user.incrementTokenVersion();
        userRepository.save(user);
    }
}