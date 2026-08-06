package com.aejimenezdev.misfinanzas.expense.application.usecases;

import com.aejimenezdev.misfinanzas.expense.domain.exceptions.ExpenseNotFoundException;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;

public class DeleteExpenseUseCase {

    private final ExpenseRepository expenseRepository;

    public DeleteExpenseUseCase(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void execute(Long userId, Long expenseId) {
        var existing = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        expenseRepository.save(existing.markDeleted());
    }
}