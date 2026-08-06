package com.aejimenezdev.misfinanzas.income.application.usecases;

import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.services.IncomeResponseAssembler;
import com.aejimenezdev.misfinanzas.income.domain.exceptions.IncomeNotFoundException;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

public class GetIncomeUseCase {

    private final IncomeRepository incomeRepository;
    private final IncomeResponseAssembler assembler;

    public GetIncomeUseCase(IncomeRepository incomeRepository, IncomeResponseAssembler assembler) {
        this.incomeRepository = incomeRepository;
        this.assembler = assembler;
    }

    public IncomeResponse execute(Long userId, Long incomeId) {
        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));
        return assembler.toResponse(income);
    }
}