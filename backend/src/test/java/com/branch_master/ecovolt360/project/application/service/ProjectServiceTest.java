package com.branch_master.ecovolt360.project.application.service;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.customer.domain.repository.CustomerRepository;
import com.branch_master.ecovolt360.project.application.dto.ProjectSummaryDTO;
import com.branch_master.ecovolt360.project.domain.entity.Project;
import com.branch_master.ecovolt360.project.domain.entity.ProjectStatus;
import com.branch_master.ecovolt360.project.domain.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.branch_master.ecovolt360.project.ProjectFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CustomerRepository customerRepository;

    private ProjectService service;

    private final UUID userId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ProjectService(projectRepository, customerRepository);
    }

    @Test
    void shouldListProjectsOfTheAuthenticatedCustomerWithTheirContent() {
        Project installed = project(customerId, address("Porto Alegre"),
                "Solar Residencial Ana", ProjectStatus.INSTALLED, INSTALLATION_DATE);
        Project awaiting = project(customerId, address("Canoas"),
                "Solar Comercial Ana", ProjectStatus.AWAITING_INSTALLATION, null);

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(customerId, userId)));
        when(projectRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(installed, awaiting));

        List<ProjectSummaryDTO> result = service.listCustomerProjects(userId);

        assertEquals(2, result.size());

        ProjectSummaryDTO first = result.get(0);
        assertEquals(installed.getId(), first.id());
        assertEquals("Solar Residencial Ana", first.name());
        assertEquals(ProjectStatus.INSTALLED, first.status());
        assertEquals(INSTALLATION_DATE, first.installationDate());
        assertEquals("Porto Alegre", first.address().city());
        assertEquals("90010-000", first.address().cep());

        ProjectSummaryDTO second = result.get(1);
        assertEquals(awaiting.getId(), second.id());
        assertEquals(ProjectStatus.AWAITING_INSTALLATION, second.status());
        assertEquals("Canoas", second.address().city());
    }

    @Test
    void shouldQueryProjectsByCustomerIdAndNotByUserId() {
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(customerId, userId)));
        when(projectRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of());

        service.listCustomerProjects(userId);

        verify(projectRepository).findByCustomerIdOrderByCreatedAtDesc(customerId);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoProjects() {
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(customerId, userId)));
        when(projectRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of());

        List<ProjectSummaryDTO> result = service.listCustomerProjects(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNullInstallationDateWhenNoneIsScheduled() {
        Project awaiting = project(customerId, address("Rio de Janeiro"),
                "Solar Comercial Carla", ProjectStatus.AWAITING_INSTALLATION, null);

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(customerId, userId)));
        when(projectRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(awaiting));

        ProjectSummaryDTO result = service.listCustomerProjects(userId).getFirst();

        assertNull(result.installationDate());
        assertEquals(ProjectStatus.AWAITING_INSTALLATION, result.status());
    }

    @Test
    void shouldReturnNullAddressWhenProjectHasNoAddress() {
        Project noAddress = project(customerId, null,
                "Solar Residencial Diego", ProjectStatus.INSTALLED, INSTALLATION_DATE);

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer(customerId, userId)));
        when(projectRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)).thenReturn(List.of(noAddress));

        ProjectSummaryDTO result = service.listCustomerProjects(userId).getFirst();

        assertNull(result.address());
    }

    @Test
    void shouldThrowForbiddenWhenAuthenticatedUserIsNotACustomer() {
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> service.listCustomerProjects(userId));

        verifyNoInteractions(projectRepository);
    }
}
