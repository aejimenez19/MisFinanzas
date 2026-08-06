package com.aejimenezdev.misfinanzas.dashboard.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.dashboard.application.dto.DashboardSummaryResponse;
import com.aejimenezdev.misfinanzas.dashboard.application.services.DashboardAssembler;
import com.aejimenezdev.misfinanzas.dashboard.application.usecases.GetDashboardSummaryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;
    private final DashboardAssembler assembler;

    public DashboardController(GetDashboardSummaryUseCase getDashboardSummaryUseCase,
                               DashboardAssembler assembler) {
        this.getDashboardSummaryUseCase = getDashboardSummaryUseCase;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<DashboardSummaryResponse> summary(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(assembler.toResponse(getDashboardSummaryUseCase.execute(principal.id())));
    }
}
