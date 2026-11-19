package com.branch_master.ecovolt360.user.infrastructure.config;

import com.branch_master.ecovolt360.user.application.port.PasswordHasher;
import com.branch_master.ecovolt360.user.application.service.ManagerSeedService;
import com.branch_master.ecovolt360.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

    @Bean
    public ManagerSeedService managerSeedService(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        return new ManagerSeedService(userRepository, passwordHasher);
    }
}
