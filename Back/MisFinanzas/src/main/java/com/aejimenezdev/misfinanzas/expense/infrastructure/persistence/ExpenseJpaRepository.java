package com.aejimenezdev.misfinanzas.expense.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseJpaEntity, Long> {

    Optional<ExpenseJpaEntity> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    List<ExpenseJpaEntity> findByUserIdAndDeletedFalseOrderByMovementDateDesc(Long userId);

    @Query("""
            SELECT e FROM ExpenseJpaEntity e
            WHERE e.userId = :userId AND e.deleted = false
              AND (:categoryId IS NULL OR e.categoryId = :categoryId)
              AND (:from IS NULL OR e.movementDate >= :from)
              AND (:to IS NULL OR e.movementDate <= :to)
            ORDER BY e.movementDate DESC, e.id DESC
            """)
    List<ExpenseJpaEntity> search(@Param("userId") Long userId,
                                  @Param("categoryId") Long categoryId,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to);
}