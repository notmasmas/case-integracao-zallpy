package com.branch_master.ecovolt360.auth.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.application.service.SessionActivityService;
import com.branch_master.ecovolt360.auth.presentation.dto.SessionActivityResponse;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de atividade da sessão. Não recebem identificadores: o usuário é sempre o da
 * sessão autenticada, então não é possível registrar ou consultar a atividade de outra sessão.
 */
@RestController
@RequestMapping("/session/activity")
public class SessionActivityController {

    private final SessionActivityService sessionActivityService;

    public SessionActivityController(SessionActivityService sessionActivityService) {
        this.sessionActivityService = sessionActivityService;
    }

    /** Registra uma atividade do usuário autenticado. */
    @PostMapping
    public SessionActivityResponse register(@CurrentUser AuthenticatedUser user) {
        return SessionActivityResponse.active(sessionActivityService.register(user.userId()));
    }

    /** Consulta a situação de atividade da sessão, sem renová-la. */
    @GetMapping
    public SessionActivityResponse status(@CurrentUser AuthenticatedUser user) {
        return SessionActivityResponse.active(sessionActivityService.status(user.userId()));
    }
}
