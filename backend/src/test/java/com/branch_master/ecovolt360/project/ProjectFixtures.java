package com.branch_master.ecovolt360.project;

import com.branch_master.ecovolt360.address.domain.entity.Address;
import com.branch_master.ecovolt360.customer.domain.entity.Customer;
import com.branch_master.ecovolt360.project.domain.entity.Project;
import com.branch_master.ecovolt360.project.domain.entity.ProjectStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public final class ProjectFixtures {

    public static final ZonedDateTime CREATED_AT = ZonedDateTime.of(2026, 5, 2, 10, 0, 0, 0, ZoneOffset.UTC);
    public static final ZonedDateTime INSTALLATION_DATE = ZonedDateTime.of(2026, 6, 10, 9, 0, 0, 0, ZoneOffset.UTC);

    private ProjectFixtures() {
    }

    public static Customer customer(UUID customerId, UUID userId) {
        Customer customer = new Customer(userId);
        customer.setId(customerId);
        return customer;
    }

    public static Address address(String city) {
        Address address = new Address(
                "90010-000", "RS", city, "Centro Histórico", "Rua dos Andradas", "1001", "Apto 302"
        );
        address.setId(UUID.randomUUID());
        return address;
    }

    public static Project project(
            UUID customerId,
            Address address,
            String name,
            ProjectStatus status,
            ZonedDateTime installationDate
    ) {
        Project project = new Project();
        ReflectionTestUtils.setField(project, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(project, "customerId", customerId);
        ReflectionTestUtils.setField(project, "addressId", address == null ? null : address.getId());
        ReflectionTestUtils.setField(project, "address", address);
        ReflectionTestUtils.setField(project, "name", name);
        ReflectionTestUtils.setField(project, "status", status);
        ReflectionTestUtils.setField(project, "installationDate", installationDate);
        ReflectionTestUtils.setField(project, "createdAt", CREATED_AT);
        ReflectionTestUtils.setField(project, "updatedAt", CREATED_AT);
        return project;
    }
}
