package com.aejimenezdev.misfinanzas.expense.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        Long categoryId,
        String categoryName,
        LocalDate movementDate,
        Instant createdAt,
        Instant updatedAt) {
}