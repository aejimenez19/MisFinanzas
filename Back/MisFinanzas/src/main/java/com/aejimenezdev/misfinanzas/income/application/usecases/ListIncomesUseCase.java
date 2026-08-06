package com.aejimenezdev.misfinanzas.income.application.usecases;

import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.application.services.IncomeResponseAssembler;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

import java.time.LocalDate;
import java.util.List;

public class ListIncomesUseCase {

    private final IncomeRepository incomeRepository;
    private final IncomeResponseAssembler assembler;

    public ListIncomesUseCase(IncomeRepository incomeRepository, IncomeResponseAssembler assembler) {
        this.incomeRepository = incomeRepository;
        this.assembler = assembler;
    }

    public List<IncomeResponse> execute(Long userId, Long categoryId, LocalDate from, LocalDate to) {
        List<Income> incomes = (categoryId == null && from == null && to == null)
                ? incomeRepository.findByUserId(userId)
                : incomeRepository.search(userId, categoryId, from, to);
        return assembler.toResponseList(incomes);
    }
}