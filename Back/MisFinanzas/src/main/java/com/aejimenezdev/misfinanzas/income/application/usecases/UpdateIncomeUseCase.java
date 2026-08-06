package com.aejimenezdev.misfinanzas.income.application.usecases;

import com.aejimenezdev.misfinanzas.income.application.dto.IncomeRequest;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.services.IncomeResponseAssembler;
import com.aejimenezdev.misfinanzas.income.domain.exceptions.IncomeNotFoundException;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

public class UpdateIncomeUseCase {

    private final IncomeRepository incomeRepository;
    private final IncomeResponseAssembler assembler;

    public UpdateIncomeUseCase(IncomeRepository incomeRepository, IncomeResponseAssembler assembler) {
        this.incomeRepository = incomeRepository;
        this.assembler = assembler;
    }

    public IncomeResponse execute(Long userId, Long incomeId, IncomeRequest request) {
        assembler.requireIncomeCategory(request.categoryId());
        Income existing = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));
        Income updated = existing.update(request.description(), request.amount(),
                request.categoryId(), request.movementDate());
        return assembler.toResponse(incomeRepository.save(updated));
    }
}