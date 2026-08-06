package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "credit_card_purchases")
public class CreditCardPurchaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_card_id", nullable = false)
    private Long creditCardId;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "billing_cycle")
    private LocalDate billingCycle;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    protected CreditCardPurchaseJpaEntity() {
    }

    public CreditCardPurchaseJpaEntity(Long id, Long creditCardId, String description, BigDecimal amount,
                                       LocalDate purchaseDate, LocalDate billingCycle,
                                       Instant createdAt, boolean deleted) {
        this.id = id;
        this.creditCardId = creditCardId;
        this.description = description;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.billingCycle = billingCycle;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getBillingCycle() {
        return billingCycle;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
