package com.aejimenezdev.misfinanzas.expense.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseRequest;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.usecases.CreateExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.DeleteExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.GetExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.ListExpensesUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.UpdateExpenseUseCase;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final CreateExpenseUseCase createExpenseUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final DeleteExpenseUseCase deleteExpenseUseCase;
    private final ListExpensesUseCase listExpensesUseCase;
    private final GetExpenseUseCase getExpenseUseCase;

    public ExpenseController(CreateExpenseUseCase createExpenseUseCase, UpdateExpenseUseCase updateExpenseUseCase,
                             DeleteExpenseUseCase deleteExpenseUseCase, ListExpensesUseCase listExpensesUseCase,
                             GetExpenseUseCase getExpenseUseCase) {
        this.createExpenseUseCase = createExpenseUseCase;
        this.updateExpenseUseCase = updateExpenseUseCase;
        this.deleteExpenseUseCase = deleteExpenseUseCase;
        this.listExpensesUseCase = listExpensesUseCase;
        this.getExpenseUseCase = getExpenseUseCase;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createExpenseUseCase.execute(principal.id(), request));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> list(@AuthenticationPrincipal AuthenticatedUser principal,
                                                      @RequestParam(required = false) Long categoryId,
                                                      @RequestParam(required = false)
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                      @RequestParam(required = false)
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(listExpensesUseCase.execute(principal.id(), categoryId, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(@AuthenticationPrincipal AuthenticatedUser principal,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(getExpenseUseCase.execute(principal.id(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(updateExpenseUseCase.execute(principal.id(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Long id) {
        deleteExpenseUseCase.execute(principal.id(), id);
        return ResponseEntity.noContent().build();
    }
}