package com.branch_master.ecovolt360.auth.infrastructure.security;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.domain.entity.Role;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenVerifier tokenVerifier;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(tokenVerifier, authenticationEntryPoint);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenVerifier, never()).verify(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenVerifier, never()).verify(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateValidBearerToken() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AuthenticatedUser user = new AuthenticatedUser(userId, Role.CUSTOMER);
        when(tokenVerifier.verify("valid-token")).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(user, authentication.getPrincipal());
        assertEquals("ROLE_CUSTOMER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldDelegateToEntryPointWhenTokenIsInvalid() throws Exception {
        when(tokenVerifier.verify("bad-token")).thenThrow(new InvalidTokenException());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(authenticationEntryPoint).commence(eq(request), eq(response), any(BadCredentialsException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
