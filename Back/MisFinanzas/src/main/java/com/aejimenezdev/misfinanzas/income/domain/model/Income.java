package com.aejimenezdev.misfinanzas.income.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record Income(
        Long id,
        Long userId,
        String description,
        BigDecimal amount,
        Long categoryId,
        LocalDate movementDate,
        Instant createdAt,
        Instant updatedAt,
        boolean deleted) {

    public static Income create(Long userId, String description, BigDecimal amount,
                                Long categoryId, LocalDate movementDate) {
        Instant now = Instant.now();
        return new Income(null, userId, description, amount, categoryId, movementDate, now, now, false);
    }

    public Income update(String description, BigDecimal amount, Long categoryId, LocalDate movementDate) {
        return new Income(id, userId, description, amount, categoryId, movementDate, createdAt, Instant.now(), deleted);
    }

    public Income markDeleted() {
        return new Income(id, userId, description, amount, categoryId, movementDate, createdAt, Instant.now(), true);
    }
}
