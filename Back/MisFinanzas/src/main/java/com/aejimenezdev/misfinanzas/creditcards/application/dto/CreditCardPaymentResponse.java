package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CreditCardPaymentResponse(
        Long id,
        Long creditCardId,
        BigDecimal amount,
        LocalDate paymentDate,
        Instant createdAt) {
}
