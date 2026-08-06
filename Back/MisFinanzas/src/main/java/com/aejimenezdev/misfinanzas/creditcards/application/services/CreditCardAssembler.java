package com.aejimenezdev.misfinanzas.creditcards.application.services;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardStatementResponse;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CreditCardAssembler {

    private final CreditCardPurchaseRepository purchaseRepository;
    private final CreditCardPaymentRepository paymentRepository;

    public CreditCardAssembler(CreditCardPurchaseRepository purchaseRepository,
                               CreditCardPaymentRepository paymentRepository) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
    }

    public BigDecimal pendingBalance(Long creditCardId) {
        BigDecimal purchases = purchaseRepository.findByCreditCardId(creditCardId).stream()
                .map(CreditCardPurchase::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal payments = paymentRepository.findByCreditCardId(creditCardId).stream()
                .map(CreditCardPayment::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return purchases.subtract(payments).max(BigDecimal.ZERO);
    }

    public BigDecimal limitUsagePercent(BigDecimal pending, BigDecimal limit) {
        if (limit.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return pending.multiply(BigDecimal.valueOf(100))
                .divide(limit, 2, RoundingMode.HALF_UP);
    }

    public LocalDate nextPaymentDate(int paymentDay, LocalDate today) {
        YearMonth current = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDate due = withDayClamped(current, paymentDay);
        if (due.isBefore(today)) {
            due = withDayClamped(current.plusMonths(1), paymentDay);
        }
        return due;
    }

    public LocalDate billingCycle(LocalDate purchaseDate, int cutoffDay) {
        YearMonth month = YearMonth.of(purchaseDate.getYear(), purchaseDate.getMonthValue());
        LocalDate cycle = withDayClamped(month, cutoffDay);
        if (purchaseDate.isAfter(cycle)) {
            cycle = withDayClamped(month.plusMonths(1), cutoffDay);
        }
        return cycle;
    }

    LocalDate nextCutoff(int cutoffDay, LocalDate today) {
        YearMonth month = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDate candidate = withDayClamped(month, cutoffDay);
        if (candidate.isBefore(today)) {
            candidate = withDayClamped(month.plusMonths(1), cutoffDay);
        }
        return candidate;
    }

    LocalDate paymentDueDate(LocalDate cutoff, int paymentDay) {
        YearMonth month = YearMonth.from(cutoff);
        LocalDate due = withDayClamped(month, paymentDay);
        if (paymentDay <= cutoff.getDayOfMonth()) {
            due = withDayClamped(month.plusMonths(1), paymentDay);
        }
        return due;
    }

    BilledBreakdown cycleBreakdown(Long creditCardId, int cutoffDay, int paymentDay, LocalDate today) {
        LocalDate currentCutoff = nextCutoff(cutoffDay, today);
        BigDecimal billed = BigDecimal.ZERO;
        BigDecimal unbilled = BigDecimal.ZERO;
        for (CreditCardStatementResponse statement : statements(creditCardId, cutoffDay, paymentDay, today)) {
            if (statement.cutoffDate().isBefore(currentCutoff)) {
                billed = billed.add(statement.remainingAmount());
            } else {
                unbilled = unbilled.add(statement.remainingAmount());
            }
        }
        return new BilledBreakdown(billed, unbilled);
    }

    public BigDecimal billedBalance(Long creditCardId, int cutoffDay, int paymentDay, LocalDate today) {
        return cycleBreakdown(creditCardId, cutoffDay, paymentDay, today).billed();
    }

    List<CreditCardStatementResponse> statements(Long creditCardId, int cutoffDay, int paymentDay,
                                                        LocalDate today) {
        Map<LocalDate, BigDecimal> byCycle = new TreeMap<>();
        purchaseRepository.findByCreditCardId(creditCardId).forEach(purchase -> {
            LocalDate cycle = purchase.billingCycle() == null
                    ? billingCycle(purchase.purchaseDate(), cutoffDay)
                    : purchase.billingCycle();
            byCycle.merge(cycle, purchase.amount(), BigDecimal::add);
        });

        List<CreditCardStatementResponse> statements = new ArrayList<>();
        byCycle.forEach((cycle, total) -> statements.add(
                new CreditCardStatementResponse(cycle, paymentDueDate(cycle, paymentDay), total,
                        BigDecimal.ZERO, total)));

        Map<LocalDate, BigDecimal> remaining = new TreeMap<>();
        List<CreditCardStatementResponse> ordered = statements.stream()
                .sorted(java.util.Comparator.comparing(CreditCardStatementResponse::cutoffDate))
                .toList();
        ordered.forEach(statement -> remaining.put(statement.cutoffDate(), statement.totalAmount()));

        for (CreditCardPayment payment : paymentRepository.findByCreditCardId(creditCardId)) {
            BigDecimal paymentAmount = payment.amount();
            for (CreditCardStatementResponse statement : ordered) {
                if (paymentAmount.signum() <= 0) {
                    break;
                }
                BigDecimal debt = remaining.get(statement.cutoffDate());
                if (debt.signum() <= 0) {
                    continue;
                }
                BigDecimal applied = paymentAmount.min(debt);
                remaining.put(statement.cutoffDate(), debt.subtract(applied));
                paymentAmount = paymentAmount.subtract(applied);
            }
        }

        LocalDate currentCutoff = nextCutoff(cutoffDay, today);
        List<CreditCardStatementResponse> result = new ArrayList<>();
        boolean includeUpcoming = true;
        for (CreditCardStatementResponse statement : ordered) {
            BigDecimal remainingAmount = remaining.get(statement.cutoffDate()).max(BigDecimal.ZERO);
            if (remainingAmount.signum() > 0) {
                addStatement(result, statement, remainingAmount);
                continue;
            }
            if (includeUpcoming && statement.cutoffDate().compareTo(currentCutoff) >= 0
                    && statement.totalAmount().signum() > 0) {
                addStatement(result, statement, remainingAmount);
                includeUpcoming = false;
            }
        }
        return result;
    }

    private void addStatement(List<CreditCardStatementResponse> result,
                              CreditCardStatementResponse statement, BigDecimal remainingAmount) {
        BigDecimal paidAmount = statement.totalAmount().subtract(remainingAmount).max(BigDecimal.ZERO);
        result.add(new CreditCardStatementResponse(statement.cutoffDate(), statement.dueDate(),
                statement.totalAmount(), paidAmount, remainingAmount.max(BigDecimal.ZERO)));
    }

    public CreditCardResponse toCardResponse(CreditCard card, LocalDate today) {
        BigDecimal pending = pendingBalance(card.id());
        List<CreditCardStatementResponse> statements = statements(card.id(), card.cutoffDay(), card.paymentDay(), today);
        BilledBreakdown breakdown = cycleBreakdown(card.id(), card.cutoffDay(), card.paymentDay(), today);
        LocalDate due = statements.size() > 0 ? statements.get(0).dueDate()
                : nextPaymentDate(card.paymentDay(), today);
        return new CreditCardResponse(
                card.id(),
                card.name(),
                card.bank(),
                card.lastFourDigits(),
                card.creditLimit(),
                card.cutoffDay(),
                card.paymentDay(),
                card.status(),
                pending,
                limitUsagePercent(pending, card.creditLimit()),
                due,
                card.createdAt(),
                card.updatedAt(),
                breakdown.billed(),
                breakdown.unbilled(),
                statements);
    }

    public List<CreditCardResponse> toCardResponseList(List<CreditCard> cards) {
        LocalDate today = LocalDate.now();
        return cards.stream().map(card -> toCardResponse(card, today)).toList();
    }

    public CreditCardPurchaseResponse toPurchaseResponse(CreditCardPurchase purchase) {
        return new CreditCardPurchaseResponse(purchase.id(), purchase.creditCardId(), purchase.description(),
                purchase.amount(), purchase.purchaseDate(), purchase.billingCycle(), purchase.createdAt());
    }

    public CreditCardPaymentResponse toPaymentResponse(CreditCardPayment payment) {
        return new CreditCardPaymentResponse(payment.id(), payment.creditCardId(), payment.amount(),
                payment.paymentDate(), payment.createdAt());
    }

    private LocalDate withDayClamped(YearMonth month, int day) {
        return month.atDay(Math.min(day, month.lengthOfMonth()));
    }

    record BilledBreakdown(BigDecimal billed, BigDecimal unbilled) {
    }
}
