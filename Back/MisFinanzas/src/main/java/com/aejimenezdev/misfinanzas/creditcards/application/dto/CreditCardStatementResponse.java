package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardStatementResponse(
        LocalDate cutoffDate,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount) {
}