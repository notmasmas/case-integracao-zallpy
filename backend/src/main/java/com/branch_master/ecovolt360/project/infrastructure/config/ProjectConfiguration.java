package com.branch_master.ecovolt360.project.infrastructure.config;

import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.project.application.service.ProjectService;
import com.branch_master.ecovolt360.project.domain.repository.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfiguration {

    @Bean
    public ProjectService projectService(
            ProjectRepository projectRepository,
            CustomerRepository customerRepository
    ) {
        return new ProjectService(projectRepository, customerRepository);
    }
}
