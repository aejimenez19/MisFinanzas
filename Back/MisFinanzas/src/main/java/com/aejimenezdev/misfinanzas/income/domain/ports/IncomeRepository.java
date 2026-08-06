package com.aejimenezdev.misfinanzas.income.domain.ports;

import com.aejimenezdev.misfinanzas.income.domain.model.Income;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository {

    Income save(Income income);

    Optional<Income> findByIdAndUserId(Long id, Long userId);

    List<Income> findByUserId(Long userId);

    List<Income> search(Long userId, Long categoryId, LocalDate from, LocalDate to);
}