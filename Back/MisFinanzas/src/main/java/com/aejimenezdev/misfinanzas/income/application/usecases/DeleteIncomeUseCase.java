package com.aejimenezdev.misfinanzas.income.application.usecases;

import com.aejimenezdev.misfinanzas.income.domain.exceptions.IncomeNotFoundException;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

public class DeleteIncomeUseCase {

    private final IncomeRepository incomeRepository;

    public DeleteIncomeUseCase(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public void execute(Long userId, Long incomeId) {
        var existing = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));
        incomeRepository.save(existing.markDeleted());
    }
}