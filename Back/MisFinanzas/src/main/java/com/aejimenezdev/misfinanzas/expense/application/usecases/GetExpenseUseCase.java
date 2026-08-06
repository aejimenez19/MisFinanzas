package com.aejimenezdev.misfinanzas.expense.application.usecases;

import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.services.ExpenseResponseAssembler;
import com.aejimenezdev.misfinanzas.expense.domain.exceptions.ExpenseNotFoundException;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;

public class GetExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseResponseAssembler assembler;

    public GetExpenseUseCase(ExpenseRepository expenseRepository, ExpenseResponseAssembler assembler) {
        this.expenseRepository = expenseRepository;
        this.assembler = assembler;
    }

    public ExpenseResponse execute(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));
        return assembler.toResponse(expense);
    }
}