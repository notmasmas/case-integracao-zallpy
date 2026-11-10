package com.branch_master.ecovolt360.project.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.presentation.exception.AccessExceptionHandler;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import com.branch_master.ecovolt360.project.application.dto.ProjectAddressDTO;
import com.branch_master.ecovolt360.project.application.dto.ProjectSummaryDTO;
import com.branch_master.ecovolt360.project.application.service.ProjectService;
import com.branch_master.ecovolt360.project.domain.entity.ProjectStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static com.branch_master.ecovolt360.project.ProjectFixtures.CREATED_AT;
import static com.branch_master.ecovolt360.project.ProjectFixtures.INSTALLATION_DATE;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerProjectControllerTest {

    @Mock
    private ProjectService projectService;

    private MockMvc mockMvc;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CustomerProjectController(projectService))
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new AccessExceptionHandler())
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAsCustomer() {
        AuthenticatedUser user = new AuthenticatedUser(userId, Role.CUSTOMER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                )
        );
    }

    @Test
    void shouldReturnCustomerProjectsWithStatusAndInstallationDate() throws Exception {
        authenticateAsCustomer();

        ProjectSummaryDTO installed = new ProjectSummaryDTO(
                UUID.randomUUID(),
                "Solar Residencial Ana",
                ProjectStatus.INSTALLED,
                INSTALLATION_DATE,
                new ProjectAddressDTO("90010-000", "RS", "Porto Alegre", "Centro Histórico",
                        "Rua dos Andradas", "1001", "Apto 302"),
                CREATED_AT,
                CREATED_AT
        );
        ProjectSummaryDTO awaiting = new ProjectSummaryDTO(
                UUID.randomUUID(),
                "Solar Comercial Ana",
                ProjectStatus.AWAITING_INSTALLATION,
                null,
                null,
                CREATED_AT,
                CREATED_AT
        );
        when(projectService.listCustomerProjects(userId)).thenReturn(List.of(installed, awaiting));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(installed.id().toString()))
                .andExpect(jsonPath("$[0].name").value("Solar Residencial Ana"))
                .andExpect(jsonPath("$[0].status").value("INSTALLED"))
                .andExpect(jsonPath("$[0].installationDate").value(startsWith("2026-06-10T09:00")))
                .andExpect(jsonPath("$[0].address.city").value("Porto Alegre"))
                .andExpect(jsonPath("$[0].address.street").value("Rua dos Andradas"))
                .andExpect(jsonPath("$[1].status").value("AWAITING_INSTALLATION"))
                .andExpect(jsonPath("$[1].installationDate").value(nullValue()))
                .andExpect(jsonPath("$[1].address").value(nullValue()));
    }

    @Test
    void shouldNotExposeInternalOwnershipFields() throws Exception {
        authenticateAsCustomer();

        ProjectSummaryDTO project = new ProjectSummaryDTO(
                UUID.randomUUID(), "Solar Residencial Ana", ProjectStatus.INSTALLED,
                INSTALLATION_DATE, null, CREATED_AT, CREATED_AT
        );
        when(projectService.listCustomerProjects(userId)).thenReturn(List.of(project));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").doesNotExist())
                .andExpect(jsonPath("$[0].addressId").doesNotExist());
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoProjects() throws Exception {
        authenticateAsCustomer();
        when(projectService.listCustomerProjects(userId)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldPassTheAuthenticatedUserIdToTheService() throws Exception {
        authenticateAsCustomer();
        when(projectService.listCustomerProjects(userId)).thenReturn(List.of());

        mockMvc.perform(get("/projects")).andExpect(status().isOk());

        verify(projectService).listCustomerProjects(userId);
        verifyNoMoreInteractions(projectService);
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoCustomerRecord() throws Exception {
        authenticateAsCustomer();
        when(projectService.listCustomerProjects(userId)).thenThrow(new ForbiddenException());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldReturnUnauthorizedWhenThereIsNoAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verifyNoInteractions(projectService);
    }
}
