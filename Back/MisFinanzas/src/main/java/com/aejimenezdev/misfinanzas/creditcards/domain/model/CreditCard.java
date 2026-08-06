package com.aejimenezdev.misfinanzas.creditcards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditCard(
        Long id,
        Long userId,
        String name,
        String bank,
        String lastFourDigits,
        BigDecimal creditLimit,
        int cutoffDay,
        int paymentDay,
        CreditCardStatus status,
        Instant createdAt,
        Instant updatedAt,
        boolean deleted) {

    public static CreditCard create(Long userId, String name, String bank, String lastFourDigits,
                                    BigDecimal creditLimit, int cutoffDay, int paymentDay) {
        Instant now = Instant.now();
        return new CreditCard(null, userId, name, bank, lastFourDigits, creditLimit, cutoffDay,
                paymentDay, CreditCardStatus.ACTIVE, now, now, false);
    }

    public CreditCard update(String name, String bank, String lastFourDigits, BigDecimal creditLimit,
                             int cutoffDay, int paymentDay, CreditCardStatus status) {
        return new CreditCard(id, userId, name, bank, lastFourDigits, creditLimit, cutoffDay,
                paymentDay, status, createdAt, Instant.now(), deleted);
    }

    public CreditCard markDeleted() {
        return new CreditCard(id, userId, name, bank, lastFourDigits, creditLimit, cutoffDay,
                paymentDay, status, createdAt, Instant.now(), true);
    }
}