package com.aejimenezdev.misfinanzas.dashboard.application.dto;

import com.aejimenezdev.misfinanzas.dashboard.domain.model.CardBalanceView;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.MonthlyFlow;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.RecentMovement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryResponse(
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
