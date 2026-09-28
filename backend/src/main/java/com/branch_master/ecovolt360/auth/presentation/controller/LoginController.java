package com.branch_master.ecovolt360.auth.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.LoginResult;
import com.branch_master.ecovolt360.auth.application.service.LoginService;
import com.branch_master.ecovolt360.auth.presentation.dto.LoginRequest;
import com.branch_master.ecovolt360.auth.presentation.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}