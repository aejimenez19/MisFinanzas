package com.aejimenezdev.misfinanzas.income.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class IncomeRepositoryAdapter implements IncomeRepository {

    private final IncomeJpaRepository jpaRepository;

    public IncomeRepositoryAdapter(IncomeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Income save(Income income) {
        return toDomain(jpaRepository.save(toEntity(income)));
    }

    @Override
    public Optional<Income> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserIdAndDeletedFalse(id, userId).map(this::toDomain);
    }

    @Override
    public List<Income> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalseOrderByMovementDateDesc(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Income> search(Long userId, Long categoryId, LocalDate from, LocalDate to) {
        return jpaRepository.search(userId, categoryId, from, to)
                .stream().map(this::toDomain).toList();
    }

    private Income toDomain(IncomeJpaEntity entity) {
        return new Income(entity.getId(), entity.getUserId(), entity.getDescription(), entity.getAmount(),
                entity.getCategoryId(), entity.getMovementDate(), entity.getCreatedAt(),
                entity.getUpdatedAt(), entity.isDeleted());
    }

    private IncomeJpaEntity toEntity(Income income) {
        return new IncomeJpaEntity(income.id(), income.userId(), income.description(), income.amount(),
                income.categoryId(), income.movementDate(), income.createdAt(),
                income.updatedAt(), income.deleted());
    }
}