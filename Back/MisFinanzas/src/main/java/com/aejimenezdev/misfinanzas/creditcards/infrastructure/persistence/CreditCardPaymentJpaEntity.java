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
@Table(name = "credit_card_payments")
public class CreditCardPaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_card_id", nullable = false)
    private Long creditCardId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    protected CreditCardPaymentJpaEntity() {
    }

    public CreditCardPaymentJpaEntity(Long id, Long creditCardId, BigDecimal amount,
                                      LocalDate paymentDate, Instant createdAt, boolean deleted) {
        this.id = id;
        this.creditCardId = creditCardId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
