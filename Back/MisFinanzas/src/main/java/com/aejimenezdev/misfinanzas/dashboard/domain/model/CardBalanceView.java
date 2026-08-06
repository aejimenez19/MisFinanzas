package com.aejimenezdev.misfinanzas.dashboard.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardBalanceView(
        Long id,
        String name,
        String bank,
        BigDecimal pendingBalance,
        BigDecimal billedBalance,
        BigDecimal creditLimit,
        BigDecimal limitUsagePercent,
        int paymentDay,
        LocalDate nextPaymentDate) {
}
