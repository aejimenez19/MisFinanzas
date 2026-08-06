package com.aejimenezdev.misfinanzas.expense.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseRequest;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.usecases.CreateExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.DeleteExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.GetExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.ListExpensesUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.UpdateExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.domain.exceptions.ExpenseNotFoundException;
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

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateExpenseUseCase createExpenseUseCase;

    @MockitoBean
    private UpdateExpenseUseCase updateExpenseUseCase;

    @MockitoBean
    private DeleteExpenseUseCase deleteExpenseUseCase;

    @MockitoBean
    private ListExpensesUseCase listExpensesUseCase;

    @MockitoBean
    private GetExpenseUseCase getExpenseUseCase;

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

    private ExpenseResponse sampleExpense() {
        Instant now = Instant.parse("2026-08-02T10:00:00Z");
        return new ExpenseResponse(1L, "Groceries", new BigDecimal("120.50"), 1L, "Food",
                LocalDate.of(2026, 8, 2), now, now);
    }

    @Test
    void createReturnsCreatedWithExpense() throws Exception {
        when(createExpenseUseCase.execute(eq(USER_ID), any(ExpenseRequest.class)))
                .thenReturn(sampleExpense());

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Groceries\",\"amount\":120.50,"
                                + "\"categoryId\":1,\"movementDate\":\"2026-08-02\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(120.50))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Food"));
    }

    @Test
    void createRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"\",\"amount\":-1,\"categoryId\":null,\"movementDate\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsExpenses() throws Exception {
        when(listExpensesUseCase.execute(eq(USER_ID), any(), any(), any()))
                .thenReturn(List.of(sampleExpense()));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Groceries"));
    }

    @Test
    void getByIdReturnsExpense() throws Exception {
        when(getExpenseUseCase.execute(USER_ID, 1L)).thenReturn(sampleExpense());

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Food"));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        when(getExpenseUseCase.execute(USER_ID, 999L)).thenThrow(new ExpenseNotFoundException("Expense not found"));

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturnsUpdatedExpense() throws Exception {
        when(updateExpenseUseCase.execute(eq(USER_ID), eq(1L), any(ExpenseRequest.class)))
                .thenReturn(sampleExpense());

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Groceries\",\"amount\":120.50,"
                                + "\"categoryId\":1,\"movementDate\":\"2026-08-02\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Groceries"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new ExpenseNotFoundException("Expense not found")).when(deleteExpenseUseCase).execute(USER_ID, 999L);

        mockMvc.perform(delete("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }
}