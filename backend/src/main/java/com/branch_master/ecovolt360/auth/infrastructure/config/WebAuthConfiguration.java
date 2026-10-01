package com.branch_master.ecovolt360.auth.infrastructure.config;

import com.branch_master.ecovolt360.auth.application.port.TokenVerifier;
import com.branch_master.ecovolt360.auth.presentation.security.AuthInterceptor;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebAuthConfiguration implements WebMvcConfigurer {

    private final TokenVerifier tokenVerifier;

    public WebAuthConfiguration(TokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(tokenVerifier))
                .addPathPatterns("/tickets/**", "/support/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
    }
}
