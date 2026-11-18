package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SCRUM-178: rotas de gestão (/manager/**) restritas ao perfil MANAGER e
 * gerente impedido de acessar as rotas exclusivas de Cliente e Suporte.
 */
@WebMvcTest(ManagerRouteSecurityTest.ManagerStubController.class)
@Import({SecurityConfiguration.class, ManagerRouteSecurityTest.ManagerStubController.class})
class ManagerRouteSecurityTest {

    private static final String MANAGER_TOKEN = "manager-token";
    private static final String CUSTOMER_TOKEN = "customer-token";
    private static final String SUPPORT_TOKEN = "support-token";
    private static final String INVALID_TOKEN = "invalid-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenVerifier tokenVerifier;

    @BeforeEach
    void setUp() {
        when(tokenVerifier.verify(MANAGER_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.MANAGER));
        when(tokenVerifier.verify(CUSTOMER_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.CUSTOMER));
        when(tokenVerifier.verify(SUPPORT_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.SUPPORT));
        when(tokenVerifier.verify(INVALID_TOKEN)).thenThrow(new InvalidTokenException());
    }

    @Test
    void shouldAllowManagerToAccessManagementRoutes() throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @ParameterizedTest
    @ValueSource(strings = {CUSTOMER_TOKEN, SUPPORT_TOKEN})
    void shouldReturnForbiddenWhenNonManagerAccessesManagementRoutes(String token) throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingManagementRoutesWithoutToken() throws Exception {
        mockMvc.perform(get("/manager/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingManagementRoutesWithInvalidToken() throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + INVALID_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/tickets", "/projects", "/support/tickets"})
    void shouldReturnForbiddenWhenManagerAccessesCustomerOrSupportRoutes(String path) throws Exception {
        mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @RestController
    static class ManagerStubController {

        @GetMapping("/manager/ping")
        String ping() {
            return "ok";
        }
    }
}
