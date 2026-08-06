package com.aejimenezdev.misfinanzas.income.application.usecases;

import com.aejimenezdev.misfinanzas.income.application.dto.IncomeRequest;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.services.IncomeResponseAssembler;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

public class CreateIncomeUseCase {

    private final IncomeRepository incomeRepository;
    private final IncomeResponseAssembler assembler;

    public CreateIncomeUseCase(IncomeRepository incomeRepository, IncomeResponseAssembler assembler) {
        this.incomeRepository = incomeRepository;
        this.assembler = assembler;
    }

    public IncomeResponse execute(Long userId, IncomeRequest request) {
        assembler.requireIncomeCategory(request.categoryId());
        Income income = Income.create(userId, request.description(), request.amount(),
                request.categoryId(), request.movementDate());
        return assembler.toResponse(incomeRepository.save(income));
    }
}