package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CreditCardResponse(
        Long id,
        String name,
        String bank,
        String lastFourDigits,
        BigDecimal creditLimit,
        int cutoffDay,
        int paymentDay,
        CreditCardStatus status,
        BigDecimal pendingBalance,
        BigDecimal limitUsagePercent,
        LocalDate nextPaymentDate,
        Instant createdAt,
        Instant updatedAt,
        BigDecimal billedAmount,
        BigDecimal unbilledAmount,
        List<CreditCardStatementResponse> statements) {
}
