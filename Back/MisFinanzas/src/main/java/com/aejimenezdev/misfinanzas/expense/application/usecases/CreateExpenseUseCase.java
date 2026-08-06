package com.aejimenezdev.misfinanzas.expense.application.usecases;

import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseRequest;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.services.ExpenseResponseAssembler;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;

public class CreateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseResponseAssembler assembler;

    public CreateExpenseUseCase(ExpenseRepository expenseRepository, ExpenseResponseAssembler assembler) {
        this.expenseRepository = expenseRepository;
        this.assembler = assembler;
    }

    public ExpenseResponse execute(Long userId, ExpenseRequest request) {
        assembler.requireExpenseCategory(request.categoryId());
        Expense expense = Expense.create(userId, request.description(), request.amount(),
                request.categoryId(), request.movementDate());
        return assembler.toResponse(expenseRepository.save(expense));
    }
}