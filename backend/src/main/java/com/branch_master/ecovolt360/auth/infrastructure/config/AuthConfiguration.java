package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfiguration {


    @Bean
    public LoginService loginService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            TokenIssuer tokenIssuer
    ){
        return new LoginService(
                userRepository,
                passwordVerifier,
                tokenIssuer
        );
    }
}