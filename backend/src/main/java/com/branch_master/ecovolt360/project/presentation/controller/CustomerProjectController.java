package com.branch_master.ecovolt360.project.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.project.application.dto.ProjectSummaryDTO;
import com.branch_master.ecovolt360.project.application.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class CustomerProjectController {

    private final ProjectService projectService;

    public CustomerProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectSummaryDTO>> listProjects(@CurrentUser AuthenticatedUser currentUser) {
        return ResponseEntity.ok(projectService.listCustomerProjects(currentUser.userId()));
    }
}
