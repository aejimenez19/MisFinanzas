package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CreditCardPurchaseResponse(
        Long id,
        Long creditCardId,
        String description,
        BigDecimal amount,
        LocalDate purchaseDate,
        LocalDate billingCycle,
        Instant createdAt) {
}
