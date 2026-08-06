package com.aejimenezdev.misfinanzas.creditcards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CreditCardPurchase(
        Long id,
        Long creditCardId,
        String description,
        BigDecimal amount,
        LocalDate purchaseDate,
        LocalDate billingCycle,
        Instant createdAt,
        boolean deleted) {

    public static CreditCardPurchase create(Long creditCardId, String description, BigDecimal amount,
                                            LocalDate purchaseDate, LocalDate billingCycle) {
        return new CreditCardPurchase(null, creditCardId, description, amount,
                purchaseDate, billingCycle, Instant.now(), false);
    }
}