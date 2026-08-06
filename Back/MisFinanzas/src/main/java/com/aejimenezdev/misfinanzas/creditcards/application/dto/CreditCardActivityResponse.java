package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardActivityResponse(
        Long id,
        String type,
        String description,
        BigDecimal amount,
        LocalDate date,
        LocalDate billingCycle) {

    public static CreditCardActivityResponse purchase(CreditCardPurchaseResponse purchase) {
        return new CreditCardActivityResponse(purchase.id(), "PURCHASE", purchase.description(),
                purchase.amount().negate(), purchase.purchaseDate(), purchase.billingCycle());
    }

    public static CreditCardActivityResponse payment(CreditCardPaymentResponse payment) {
        return new CreditCardActivityResponse(payment.id(), "PAYMENT", "Card Payment",
                payment.amount(), payment.paymentDate(), null);
    }
}