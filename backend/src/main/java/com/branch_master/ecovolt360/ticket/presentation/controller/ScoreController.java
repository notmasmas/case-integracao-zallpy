package com.branch_master.ecovolt360.ticket.presentation.controller;

import com.branch_master.ecovolt360.auth.application.dto.AuthenticatedUser;
import com.branch_master.ecovolt360.auth.presentation.security.CurrentUser;
import com.branch_master.ecovolt360.ticket.application.dto.MetricsDTO;
import com.branch_master.ecovolt360.ticket.application.service.ScoreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/support/feedback")
@AllArgsConstructor
public class ScoreController {

    ScoreService scoreService;

    @GetMapping("/metrics")
    public ResponseEntity<MetricsDTO> getMetrics (@CurrentUser AuthenticatedUser currentUser
    ) {
        MetricsDTO metrics = scoreService.calculateMetrics(currentUser.userId());

        return ResponseEntity.ok(metrics);
    }
}
