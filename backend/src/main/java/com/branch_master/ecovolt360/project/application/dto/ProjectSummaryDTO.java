package com.branch_master.ecovolt360.project.application.dto;

import com.branch_master.ecovolt360.project.domain.entity.Project;
import com.branch_master.ecovolt360.project.domain.entity.ProjectStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ProjectSummaryDTO(
        UUID id,
        String name,
        ProjectStatus status,
        ZonedDateTime installationDate,
        ProjectAddressDTO address,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
    public ProjectSummaryDTO(Project project) {
        this(
                project.getId(),
                project.getName(),
                project.getStatus(),
                project.getInstallationDate(),
                ProjectAddressDTO.from(project.getAddress()),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
