package com.aejimenezdev.misfinanzas.income.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeJpaRepository extends JpaRepository<IncomeJpaEntity, Long> {

    Optional<IncomeJpaEntity> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    List<IncomeJpaEntity> findByUserIdAndDeletedFalseOrderByMovementDateDesc(Long userId);

    @Query("""
            SELECT i FROM IncomeJpaEntity i
            WHERE i.userId = :userId AND i.deleted = false
              AND (:categoryId IS NULL OR i.categoryId = :categoryId)
              AND (:from IS NULL OR i.movementDate >= :from)
              AND (:to IS NULL OR i.movementDate <= :to)
            ORDER BY i.movementDate DESC, i.id DESC
            """)
    List<IncomeJpaEntity> search(@Param("userId") Long userId,
                                 @Param("categoryId") Long categoryId,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);
}