package com.branch_master.ecovolt360.auth.application.service;

import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.exception.InvalidCredentialsException;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

public class LoginService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final TokenIssuer tokenIssuer;
    private final Duration inactivityLimit;
    private final Clock clock;

    public LoginService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            TokenIssuer tokenIssuer
    ) {
        this(userRepository, passwordVerifier, tokenIssuer, Duration.ofMinutes(15), Clock.systemUTC());
    }

    public LoginService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            TokenIssuer tokenIssuer,
            Duration inactivityLimit,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.tokenIssuer = tokenIssuer;
        this.inactivityLimit = inactivityLimit;
        this.clock = clock;
    }

    @Transactional
    public LoginResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches =
                passwordVerifier.matches(password, user.getPassword());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        // Nova autenticação: inicia a sessão e a contagem de inatividade
        // (descartando tokens de uma sessão anterior que estava inativa).
        user.startSession(inactivityLimit, clock.instant());
        userRepository.save(user);

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