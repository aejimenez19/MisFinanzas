package com.aejimenezdev.misfinanzas.dashboard.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecentMovement(
        Long id,
        MovementType type,
        String description,
        BigDecimal amount,
        String categoryName,
        LocalDate movementDate) {
}
