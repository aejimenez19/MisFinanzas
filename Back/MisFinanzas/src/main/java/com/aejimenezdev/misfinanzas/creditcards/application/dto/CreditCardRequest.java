package com.aejimenezdev.misfinanzas.creditcards.application.dto;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreditCardRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Bank is required")
        @Size(max = 100, message = "Bank must be at most 100 characters")
        String bank,

        @Size(min = 4, max = 4, message = "Last four digits must be 4 characters")
        @Pattern(regexp = "\\d{4}", message = "Last four digits must contain only digits")
        String lastFourDigits,

        @NotNull(message = "Credit limit is required")
        @DecimalMin(value = "0.01", message = "Credit limit must be positive")
        BigDecimal creditLimit,

        @NotNull(message = "Cutoff day is required")
        @Min(value = 1, message = "Cutoff day must be between 1 and 31")
        @Max(value = 31, message = "Cutoff day must be between 1 and 31")
        Integer cutoffDay,

        @NotNull(message = "Payment day is required")
        @Min(value = 1, message = "Payment day must be between 1 and 31")
        @Max(value = 31, message = "Payment day must be between 1 and 31")
        Integer paymentDay,

        CreditCardStatus status) {
}