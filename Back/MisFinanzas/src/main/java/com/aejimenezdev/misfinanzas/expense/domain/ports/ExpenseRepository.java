package com.aejimenezdev.misfinanzas.expense.domain.ports;

import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    List<Expense> findByUserId(Long userId);

    List<Expense> search(Long userId, Long categoryId, LocalDate from, LocalDate to);
}