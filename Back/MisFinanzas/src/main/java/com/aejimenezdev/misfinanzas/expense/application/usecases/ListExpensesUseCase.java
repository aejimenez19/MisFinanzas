package com.aejimenezdev.misfinanzas.expense.application.usecases;

import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.application.services.ExpenseResponseAssembler;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;

public class ListExpensesUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseResponseAssembler assembler;

    public ListExpensesUseCase(ExpenseRepository expenseRepository, ExpenseResponseAssembler assembler) {
        this.expenseRepository = expenseRepository;
        this.assembler = assembler;
    }

    public List<ExpenseResponse> execute(Long userId, Long categoryId, LocalDate from, LocalDate to) {
        List<Expense> expenses = (categoryId == null && from == null && to == null)
                ? expenseRepository.findByUserId(userId)
                : expenseRepository.search(userId, categoryId, from, to);
        return assembler.toResponseList(expenses);
    }
}