package com.branch_master.ecovolt360.auth.presentation.controller;

import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import com.branch_master.ecovolt360.auth.domain.entity.User;
import com.branch_master.ecovolt360.auth.domain.repository.UserRepository;
import com.branch_master.ecovolt360.auth.presentation.exception.AuthExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordVerifier passwordVerifier;

    @Mock
    private TokenIssuer tokenIssuer;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LoginService service = new LoginService(
                userRepository,
                passwordVerifier,
                tokenIssuer
        );

        LoginController controller = new LoginController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void shouldReturnTokenForValidCredentials(Role role) throws Exception {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(user.getId()).thenReturn(userId);
        when(user.getName()).thenReturn("Cliente de teste");
        when(user.getRole()).thenReturn(role);
        when(user.getPassword()).thenReturn("stored-hash");

        when(userRepository.findByEmail("cliente@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordVerifier.matches("senhaTeste", "stored-hash"))
                .thenReturn(true);

        when(tokenIssuer.issue(userId, role))
                .thenReturn("test-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cliente@example.com",
                                  "password": "senhaTeste"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente de teste"))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andExpect(jsonPath("$.accessToken").value("test-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(passwordVerifier).matches("senhaTeste", "stored-hash");
        verify(tokenIssuer).issue(userId, role);
    }

    @Test
    void shouldRejectUnknownEmail() throws Exception {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "missing@example.com",
                                  "password": "senhaTeste"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("E-mail ou senha inválidos."))
                .andExpect(jsonPath("$.accessToken").doesNotExist());

        verifyNoInteractions(passwordVerifier, tokenIssuer);
    }

    @Test
    void shouldRejectWrongPassword() throws Exception {
        User user = mock(User.class);

        when(user.getPassword()).thenReturn("stored-hash");

        when(userRepository.findByEmail("cliente@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordVerifier.matches("senhaErrada", "stored-hash"))
                .thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "cliente@example.com",
                                  "password": "senhaErrada"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("E-mail ou senha inválidos."))
                .andExpect(jsonPath("$.accessToken").doesNotExist());

        verify(passwordVerifier).matches("senhaErrada", "stored-hash");
        verifyNoInteractions(tokenIssuer);
    }

    @Test
    void shouldRejectInvalidFields() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "email-invalido",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());

        verifyNoInteractions(
                userRepository,
                passwordVerifier,
                tokenIssuer
        );
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"));

        verifyNoInteractions(
                userRepository,
                passwordVerifier,
                tokenIssuer
        );
    }
}