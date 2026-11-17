package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.application.service.SessionActivityService;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class AuthConfiguration {


    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public LoginService loginService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            TokenIssuer tokenIssuer,
            Clock clock,
            @Value("${app.session.inactivity-minutes:15}") long inactivityMinutes
    ){
        return new LoginService(
                userRepository,
                passwordVerifier,
                tokenIssuer,
                Duration.ofMinutes(inactivityMinutes),
                clock
        );
    }

    @Bean
    public SessionActivityService sessionActivityService(
            UserRepository userRepository,
            Clock clock,
            @Value("${app.session.inactivity-minutes:15}") long inactivityMinutes
    ) {
        return new SessionActivityService(
                userRepository,
                Duration.ofMinutes(inactivityMinutes),
                clock
        );
    }
}