package com.aejimenezdev.misfinanzas.dashboard.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.dashboard.application.services.DashboardAssembler;
import com.aejimenezdev.misfinanzas.dashboard.application.usecases.GetDashboardSummaryUseCase;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.DashboardSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @MockitoBean
    private DashboardAssembler assembler;

    @MockitoBean
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUpSecurity() {
        AuthenticatedUser principal = new AuthenticatedUser(USER_ID, "user@example.com", "USER");
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsDashboardSummary() throws Exception {
        DashboardSummary summary = new DashboardSummary(
                new BigDecimal("3500.00"), new BigDecimal("2000.00"), new BigDecimal("500.00"),
                LocalDate.of(2026, 8, 15), List.of(), List.of(),
                new BigDecimal("0.00"), null, 0, List.of());
        when(getDashboardSummaryUseCase.execute(USER_ID)).thenReturn(summary);
        when(assembler.toResponse(summary)).thenCallRealMethod();

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableBalance").value(3500.00))
                .andExpect(jsonPath("$.monthIncomes").value(2000.00))
                .andExpect(jsonPath("$.monthExpenses").value(500.00));
    }
}
