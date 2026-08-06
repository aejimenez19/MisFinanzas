package com.aejimenezdev.misfinanzas.expense.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    public ExpenseRepositoryAdapter(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return toDomain(jpaRepository.save(toEntity(expense)));
    }

    @Override
    public Optional<Expense> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserIdAndDeletedFalse(id, userId).map(this::toDomain);
    }

    @Override
    public List<Expense> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalseOrderByMovementDateDesc(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Expense> search(Long userId, Long categoryId, LocalDate from, LocalDate to) {
        return jpaRepository.search(userId, categoryId, from, to)
                .stream().map(this::toDomain).toList();
    }

    private Expense toDomain(ExpenseJpaEntity entity) {
        return new Expense(entity.getId(), entity.getUserId(), entity.getDescription(), entity.getAmount(),
                entity.getCategoryId(), entity.getMovementDate(), entity.getCreatedAt(),
                entity.getUpdatedAt(), entity.isDeleted());
    }

    private ExpenseJpaEntity toEntity(Expense expense) {
        return new ExpenseJpaEntity(expense.id(), expense.userId(), expense.description(), expense.amount(),
                expense.categoryId(), expense.movementDate(), expense.createdAt(),
                expense.updatedAt(), expense.deleted());
    }
}