package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.infrastructure.security.JwtAuthenticationFilter;
import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String UNAUTHORIZED_MESSAGE = "Sessão inválida ou expirada. Faça login novamente.";
    private static final String FORBIDDEN_MESSAGE = "Você não tem permissão para acessar este recurso.";

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TokenVerifier tokenVerifier,
            ObjectMapper objectMapper
    ) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) ->
                write(response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", UNAUTHORIZED_MESSAGE);
        AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) ->
                write(response, objectMapper, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", FORBIDDEN_MESSAGE);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/customers").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/tickets/{ticketId}/status").hasRole("SUPPORT")
                        .requestMatchers(HttpMethod.PATCH, "/tickets/{ticketId}/evaluation").hasRole("CUSTOMER")
                        .requestMatchers("/tickets", "/tickets/**").hasRole("CUSTOMER")
                        .requestMatchers("/support", "/support/**").hasRole("SUPPORT")

                        .anyRequest().denyAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(tokenVerifier, authenticationEntryPoint),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Login is handled by LoginService");
        };
    }

    private static void write(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            int status,
            String code,
            String message
    ) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiError(code, message, Map.of()));
    }
}
