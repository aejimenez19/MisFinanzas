package com.aejimenezdev.misfinanzas.creditcards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CreditCardPayment(
        Long id,
        Long creditCardId,
        BigDecimal amount,
        LocalDate paymentDate,
        Instant createdAt,
        boolean deleted) {

    public static CreditCardPayment create(Long creditCardId, BigDecimal amount, LocalDate paymentDate) {
        return new CreditCardPayment(null, creditCardId, amount, paymentDate, Instant.now(), false);
    }
}