package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.dto.SessionActivity;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.exception.SessionExpiredException;
import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.application.service.SessionActivityService;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.presentation.controller.LoginController;
import com.branch_master.ecovolt360.auth.presentation.controller.SessionActivityController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SCRUM-178: login e sessão do gerente, rotas de gestão (/manager/**) restritas ao perfil
 * MANAGER e gerente impedido de acessar as rotas exclusivas de Cliente e Suporte.
 */
@WebMvcTest({
        ManagerRouteSecurityTest.ManagerStubController.class,
        LoginController.class,
        SessionActivityController.class
})
@Import({SecurityConfiguration.class, ManagerRouteSecurityTest.ManagerStubController.class})
class ManagerRouteSecurityTest {

    private static final String MANAGER_TOKEN = "manager-token";
    private static final String CUSTOMER_TOKEN = "customer-token";
    private static final String SUPPORT_TOKEN = "support-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String EXPIRED_SESSION_TOKEN = "expired-session-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenVerifier tokenVerifier;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private SessionActivityService sessionActivityService;

    private final UUID managerUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(tokenVerifier.verify(MANAGER_TOKEN)).thenReturn(new AuthenticatedUser(managerUserId, Role.MANAGER));
        when(tokenVerifier.verify(CUSTOMER_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.CUSTOMER));
        when(tokenVerifier.verify(SUPPORT_TOKEN)).thenReturn(new AuthenticatedUser(UUID.randomUUID(), Role.SUPPORT));
        when(tokenVerifier.verify(INVALID_TOKEN)).thenThrow(new InvalidTokenException());
        when(tokenVerifier.verify(EXPIRED_SESSION_TOKEN)).thenThrow(new SessionExpiredException());
    }

    @Test
    void shouldReturnManagerRoleOnLogin() throws Exception {
        when(loginService.login("gerente@ecovolt.com", "senhaTeste"))
                .thenReturn(new LoginResult(managerUserId, "Gerente Teste", Role.MANAGER, MANAGER_TOKEN));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "gerente@ecovolt.com",
                                  "password": "senhaTeste"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.accessToken").value(MANAGER_TOKEN));
    }

    @Test
    void shouldReturnManagerRoleForCurrentSession() throws Exception {
        mockMvc.perform(get("/session").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    void shouldAllowManagerToRenewSessionActivity() throws Exception {
        Instant now = Instant.parse("2026-11-25T12:00:00Z");
        when(sessionActivityService.register(managerUserId))
                .thenReturn(SessionActivity.of(now, Duration.ofMinutes(15), now));

        mockMvc.perform(post("/session/activity").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldAllowManagerToLogout() throws Exception {
        mockMvc.perform(post("/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isNoContent());

        verify(loginService).logout(managerUserId);
    }

    @Test
    void shouldAllowManagerToAccessManagementRoutes() throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void shouldReturnNotFoundInsteadOfForbiddenWhenManagementRouteDoesNotExistYet() throws Exception {
        mockMvc.perform(get("/manager").header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {CUSTOMER_TOKEN, SUPPORT_TOKEN})
    void shouldReturnForbiddenWhenNonManagerAccessesManagementRoutes(String token) throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Você não tem permissão para acessar este recurso."));
    }

    @ParameterizedTest
    @CsvSource({
            "GET, /manager",
            "GET, /manager/rota/ainda/inexistente",
            "POST, /manager/ping",
            "PATCH, /manager/ping",
            "DELETE, /manager/ping"
    })
    void shouldProtectEveryManagementPathAndMethod(String method, String path) throws Exception {
        HttpMethod httpMethod = HttpMethod.valueOf(method);

        mockMvc.perform(request(httpMethod, path))
                .andExpect(status().isUnauthorized());
        for (String token : new String[]{CUSTOMER_TOKEN, SUPPORT_TOKEN}) {
            mockMvc.perform(request(httpMethod, path).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
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

    @Test
    void shouldReturnSessionExpiredWhenManagerSessionIsInactive() throws Exception {
        mockMvc.perform(get("/manager/ping").header(HttpHeaders.AUTHORIZATION, "Bearer " + EXPIRED_SESSION_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("SESSION_EXPIRED"));
    }

    @ParameterizedTest
    @CsvSource({
            "GET, /tickets",
            "POST, /tickets",
            "GET, /tickets/11111111-1111-1111-1111-111111111111",
            "POST, /tickets/11111111-1111-1111-1111-111111111111/messages",
            "PATCH, /tickets/11111111-1111-1111-1111-111111111111/evaluation",
            "GET, /projects",
            "GET, /support/tickets",
            "GET, /support/tickets/all",
            "GET, /support/feedback/metrics",
            "PATCH, /support/tickets/11111111-1111-1111-1111-111111111111/status",
            "PATCH, /support/tickets/11111111-1111-1111-1111-111111111111/assign"
    })
    void shouldReturnForbiddenWhenManagerAccessesCustomerOrSupportRoutes(String method, String path) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(method), path)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + MANAGER_TOKEN))
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
