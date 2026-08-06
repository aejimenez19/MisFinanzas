package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardStatus;

@Entity
@Table(name = "credit_cards")
public class CreditCardJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "bank", nullable = false, length = 100)
    private String bank;

    @Column(name = "last_four_digits", length = 4)
    private String lastFourDigits;

    @Column(name = "credit_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "cutoff_day", nullable = false)
    private Short cutoffDay;

    @Column(name = "payment_day", nullable = false)
    private Short paymentDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CreditCardStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    protected CreditCardJpaEntity() {
    }

    public CreditCardJpaEntity(Long id, Long userId, String name, String bank, String lastFourDigits,
                               BigDecimal creditLimit, Short cutoffDay, Short paymentDay, CreditCardStatus status,
                               Instant createdAt, Instant updatedAt, boolean deleted) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bank = bank;
        this.lastFourDigits = lastFourDigits;
        this.creditLimit = creditLimit;
        this.cutoffDay = cutoffDay;
        this.paymentDay = paymentDay;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBank() {
        return bank;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public Short getCutoffDay() {
        return cutoffDay;
    }

    public Short getPaymentDay() {
        return paymentDay;
    }

    public CreditCardStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
