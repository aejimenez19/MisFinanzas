package com.aejimenezdev.misfinanzas.dashboard.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummary(
        BigDecimal availableBalance,
        BigDecimal monthIncomes,
        BigDecimal monthExpenses,
        LocalDate generatedAt,
        List<MonthlyFlow> cashflow,
        List<CardBalanceView> cards,
        BigDecimal nextPaymentAmount,
        LocalDate nextPaymentDate,
        long daysUntilDue,
        List<RecentMovement> recentMovements) {
}
