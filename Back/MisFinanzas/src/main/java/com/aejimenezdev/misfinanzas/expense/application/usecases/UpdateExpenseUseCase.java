package com.aejimenezdev.misfinanzas.expense.application.usecases;

import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseRequest;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.services.ExpenseResponseAssembler;
import com.aejimenezdev.misfinanzas.expense.domain.exceptions.ExpenseNotFoundException;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;

public class UpdateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseResponseAssembler assembler;

    public UpdateExpenseUseCase(ExpenseRepository expenseRepository, ExpenseResponseAssembler assembler) {
        this.expenseRepository = expenseRepository;
        this.assembler = assembler;
    }

    public ExpenseResponse execute(Long userId, Long expenseId, ExpenseRequest request) {
        assembler.requireExpenseCategory(request.categoryId());
        Expense existing = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        Expense updated = existing.update(request.description(), request.amount(),
                request.categoryId(), request.movementDate());
        return assembler.toResponse(expenseRepository.save(updated));
    }
}