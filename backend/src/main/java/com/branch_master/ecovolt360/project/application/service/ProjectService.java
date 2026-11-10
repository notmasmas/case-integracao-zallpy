package com.branch_master.ecovolt360.project.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.project.application.dto.ProjectSummaryDTO;
import com.branch_master.ecovolt360.project.domain.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;

    public ProjectService(ProjectRepository projectRepository, CustomerRepository customerRepository) {
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
    }

    public List<ProjectSummaryDTO> listCustomerProjects(UUID userId) {
        Customer customer = findCustomer(userId);
        return projectRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(ProjectSummaryDTO::new)
                .toList();
    }

    private Customer findCustomer(UUID userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(ForbiddenException::new);
    }
}
