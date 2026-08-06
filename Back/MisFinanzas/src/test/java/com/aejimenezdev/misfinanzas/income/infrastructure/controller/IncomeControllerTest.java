package com.aejimenezdev.misfinanzas.income.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeRequest;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.usecases.CreateIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.DeleteIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.GetIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.ListIncomesUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.UpdateIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.domain.exceptions.IncomeNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncomeControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateIncomeUseCase createIncomeUseCase;

    @MockitoBean
    private UpdateIncomeUseCase updateIncomeUseCase;

    @MockitoBean
    private DeleteIncomeUseCase deleteIncomeUseCase;

    @MockitoBean
    private ListIncomesUseCase listIncomesUseCase;

    @MockitoBean
    private GetIncomeUseCase getIncomeUseCase;

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

    private IncomeResponse sampleIncome() {
        Instant now = Instant.parse("2026-08-01T10:00:00Z");
        return new IncomeResponse(1L, "Salary", new BigDecimal("2500.00"), 1L, "Payroll",
                LocalDate.of(2026, 8, 1), now, now);
    }

    @Test
    void createReturnsCreatedWithIncome() throws Exception {
        when(createIncomeUseCase.execute(eq(USER_ID), any(IncomeRequest.class)))
                .thenReturn(sampleIncome());

        mockMvc.perform(post("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Salary\",\"amount\":2500.00,"
                                + "\"categoryId\":1,\"movementDate\":\"2026-08-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.amount").value(2500.00))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Payroll"));
    }

    @Test
    void createRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"\",\"amount\":-5,\"categoryId\":null,\"movementDate\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsIncomes() throws Exception {
        when(listIncomesUseCase.execute(eq(USER_ID), any(), any(), any()))
                .thenReturn(List.of(sampleIncome()));

        mockMvc.perform(get("/api/incomes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Salary"));
    }

    @Test
    void getByIdReturnsIncome() throws Exception {
        when(getIncomeUseCase.execute(USER_ID, 1L)).thenReturn(sampleIncome());

        mockMvc.perform(get("/api/incomes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Payroll"));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        when(getIncomeUseCase.execute(USER_ID, 999L)).thenThrow(new IncomeNotFoundException("Income not found"));

        mockMvc.perform(get("/api/incomes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturnsUpdatedIncome() throws Exception {
        when(updateIncomeUseCase.execute(eq(USER_ID), eq(1L), any(IncomeRequest.class)))
                .thenReturn(sampleIncome());

        mockMvc.perform(put("/api/incomes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Salary\",\"amount\":2500.00,"
                                + "\"categoryId\":1,\"movementDate\":\"2026-08-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Salary"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/incomes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new IncomeNotFoundException("Income not found")).when(deleteIncomeUseCase).execute(USER_ID, 999L);

        mockMvc.perform(delete("/api/incomes/999"))
                .andExpect(status().isNotFound());
    }
}