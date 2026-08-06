package com.aejimenezdev.misfinanzas.expense.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record Expense(
        Long id,
        Long userId,
        String description,
        BigDecimal amount,
        Long categoryId,
        LocalDate movementDate,
        Instant createdAt,
        Instant updatedAt,
        boolean deleted) {

    public static Expense create(Long userId, String description, BigDecimal amount,
                                 Long categoryId, LocalDate movementDate) {
        Instant now = Instant.now();
        return new Expense(null, userId, description, amount, categoryId, movementDate, now, now, false);
    }

    public Expense update(String description, BigDecimal amount, Long categoryId, LocalDate movementDate) {
        return new Expense(id, userId, description, amount, categoryId, movementDate, createdAt, Instant.now(), deleted);
    }

    public Expense markDeleted() {
        return new Expense(id, userId, description, amount, categoryId, movementDate, createdAt, Instant.now(), true);
    }
}