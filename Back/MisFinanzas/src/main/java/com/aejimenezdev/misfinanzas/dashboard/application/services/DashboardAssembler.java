package com.aejimenezdev.misfinanzas.dashboard.application.services;

import com.aejimenezdev.misfinanzas.dashboard.application.dto.DashboardSummaryResponse;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.DashboardSummary;

public class DashboardAssembler {

    public DashboardSummaryResponse toResponse(DashboardSummary summary) {
        return new DashboardSummaryResponse(
                summary.availableBalance(),
                summary.monthIncomes(),
                summary.monthExpenses(),
                summary.generatedAt(),
                summary.cashflow(),
                summary.cards(),
                summary.nextPaymentAmount(),
                summary.nextPaymentDate(),
                summary.daysUntilDue(),
                summary.recentMovements());
    }
}
