package com.aejimenezdev.misfinanzas.income.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeRequest;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.usecases.CreateIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.DeleteIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.GetIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.ListIncomesUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.UpdateIncomeUseCase;
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
@RequestMapping("/api/incomes")
public class IncomeController {

    private final CreateIncomeUseCase createIncomeUseCase;
    private final UpdateIncomeUseCase updateIncomeUseCase;
    private final DeleteIncomeUseCase deleteIncomeUseCase;
    private final ListIncomesUseCase listIncomesUseCase;
    private final GetIncomeUseCase getIncomeUseCase;

    public IncomeController(CreateIncomeUseCase createIncomeUseCase, UpdateIncomeUseCase updateIncomeUseCase,
                            DeleteIncomeUseCase deleteIncomeUseCase, ListIncomesUseCase listIncomesUseCase,
                            GetIncomeUseCase getIncomeUseCase) {
        this.createIncomeUseCase = createIncomeUseCase;
        this.updateIncomeUseCase = updateIncomeUseCase;
        this.deleteIncomeUseCase = deleteIncomeUseCase;
        this.listIncomesUseCase = listIncomesUseCase;
        this.getIncomeUseCase = getIncomeUseCase;
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> create(@AuthenticationPrincipal AuthenticatedUser principal,
                                                 @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createIncomeUseCase.execute(principal.id(), request));
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> list(@AuthenticationPrincipal AuthenticatedUser principal,
                                                     @RequestParam(required = false) Long categoryId,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(listIncomesUseCase.execute(principal.id(), categoryId, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getById(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(getIncomeUseCase.execute(principal.id(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> update(@AuthenticationPrincipal AuthenticatedUser principal,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(updateIncomeUseCase.execute(principal.id(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Long id) {
        deleteIncomeUseCase.execute(principal.id(), id);
        return ResponseEntity.noContent().build();
    }
}