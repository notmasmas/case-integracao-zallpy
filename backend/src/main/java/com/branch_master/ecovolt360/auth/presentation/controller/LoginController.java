package com.branch_master.ecovolt360.auth.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.presentation.dto.LoginRequest;
import com.branch_master.ecovolt360.auth.presentation.dto.LoginResponse;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        LoginResult result = loginService.login(
                request.email(),
                request.password()
        );

        return new LoginResponse(
                result.userId(),
                result.name(),
                result.role(),
                result.accessToken(),
                "Bearer"
        );
    }
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CurrentUser AuthenticatedUser user) {
        loginService.logout(user.userId());
    }
}