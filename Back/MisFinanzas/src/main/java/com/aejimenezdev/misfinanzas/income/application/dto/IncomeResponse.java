package com.aejimenezdev.misfinanzas.income.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record IncomeResponse(
        Long id,
        String description,
        BigDecimal amount,
        Long categoryId,
        String categoryName,
        LocalDate movementDate,
        Instant createdAt,
        Instant updatedAt) {
}
