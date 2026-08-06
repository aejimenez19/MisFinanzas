package com.aejimenezdev.misfinanzas.dashboard.domain.model;

import java.math.BigDecimal;

public record MonthlyFlow(String month, BigDecimal incomes, BigDecimal expenses) {

    public static MonthlyFlow empty(String month) {
        return new MonthlyFlow(month, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
