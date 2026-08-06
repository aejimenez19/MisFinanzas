package com.aejimenezdev.misfinanzas.creditcards.application.services;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardStatementResponse;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditCardAssemblerTest {

    @Mock
    private CreditCardPurchaseRepository purchaseRepository;

    @Mock
    private CreditCardPaymentRepository paymentRepository;

    private CreditCardAssembler assembler() {
        return new CreditCardAssembler(purchaseRepository, paymentRepository);
    }

    @Test
    void pendingBalanceIsPurchasesMinusPayments() {
        when(purchaseRepository.findByCreditCardId(1L))
                .thenReturn(List.of(purchase("100.00"), purchase("50.00")));
        when(paymentRepository.findByCreditCardId(1L))
                .thenReturn(List.of(payment("30.00")));

        BigDecimal pending = assembler().pendingBalance(1L);

        assertThat(pending).isEqualByComparingTo("120.00");
    }

    @Test
    void pendingBalanceNeverBelowZero() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(purchase("20.00")));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of(payment("100.00")));

        BigDecimal pending = assembler().pendingBalance(1L);

        assertThat(pending).isEqualByComparingTo("0.00");
    }

    @Test
    void limitUsagePercentIsPendingOverLimit() {
        assertThat(assembler().limitUsagePercent(new BigDecimal("34.20"), new BigDecimal("100.00")))
                .isEqualByComparingTo("34.20");
        assertThat(assembler().limitUsagePercent(new BigDecimal("0.00"), new BigDecimal("100.00")))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void nextPaymentDateUsesCurrentMonthWhenDayIsTodayOrLater() {
        LocalDate today = LocalDate.of(2026, 8, 10);
        assertThat(assembler().nextPaymentDate(15, today)).isEqualTo(LocalDate.of(2026, 8, 15));
        assertThat(assembler().nextPaymentDate(10, today)).isEqualTo(LocalDate.of(2026, 8, 10));
    }

    @Test
    void nextPaymentDateMovesToNextMonthWhenDayPassed() {
        LocalDate today = LocalDate.of(2026, 8, 20);
        assertThat(assembler().nextPaymentDate(15, today)).isEqualTo(LocalDate.of(2026, 9, 15));
    }

    @Test
    void nextPaymentDateClampsToLastDayOfMonth() {
        LocalDate today = LocalDate.of(2026, 2, 10);
        assertThat(assembler().nextPaymentDate(31, today)).isEqualTo(LocalDate.of(2026, 2, 28));
    }

    @Test
    void billingCycleUsesSameMonthWhenPurchaseOnOrBeforeCutoff() {
        assertThat(assembler().billingCycle(LocalDate.of(2026, 8, 10), 10))
                .isEqualTo(LocalDate.of(2026, 8, 10));
        assertThat(assembler().billingCycle(LocalDate.of(2026, 8, 5), 10))
                .isEqualTo(LocalDate.of(2026, 8, 10));
    }

    @Test
    void billingCycleMovesToNextMonthWhenPurchaseAfterCutoff() {
        assertThat(assembler().billingCycle(LocalDate.of(2026, 8, 11), 10))
                .isEqualTo(LocalDate.of(2026, 9, 10));
    }

    @Test
    void nextCutoffUsesCurrentMonthWhenDayIsTodayOrLater() {
        LocalDate today = LocalDate.of(2026, 8, 10);
        assertThat(assembler().nextCutoff(10, today)).isEqualTo(LocalDate.of(2026, 8, 10));
        assertThat(assembler().nextCutoff(10, LocalDate.of(2026, 8, 5))).isEqualTo(LocalDate.of(2026, 8, 10));
    }

    @Test
    void nextCutoffMovesToNextMonthWhenDayPassed() {
        assertThat(assembler().nextCutoff(10, LocalDate.of(2026, 8, 11)))
                .isEqualTo(LocalDate.of(2026, 9, 10));
    }

    @Test
    void paymentDueDateSameMonthWhenPaymentDayAfterCutoff() {
        assertThat(assembler().paymentDueDate(LocalDate.of(2026, 8, 10), 15))
                .isEqualTo(LocalDate.of(2026, 8, 15));
    }

    @Test
    void paymentDueDateMovesToNextMonthWhenPaymentDayNotAfterCutoff() {
        assertThat(assembler().paymentDueDate(LocalDate.of(2026, 8, 10), 5))
                .isEqualTo(LocalDate.of(2026, 9, 5));
    }

    @Test
    void cycleBreakdownSplitsBilledAndUnbilled() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("100.00", LocalDate.of(2026, 8, 10)),
                purchase("50.00", LocalDate.of(2026, 10, 10))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of(payment("30.00")));

        CreditCardAssembler.BilledBreakdown breakdown =
                assembler().cycleBreakdown(1L, 10, 17, LocalDate.of(2026, 8, 15));

        assertThat(breakdown.billed()).isEqualByComparingTo("70.00");
        assertThat(breakdown.unbilled()).isEqualByComparingTo("50.00");
    }

    @Test
    void cycleBreakdownAppliesPaymentsToOldestCycleFirst() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("100.00", LocalDate.of(2026, 8, 10)),
                purchase("50.00", LocalDate.of(2026, 10, 10))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of(payment("120.00")));

        CreditCardAssembler.BilledBreakdown breakdown =
                assembler().cycleBreakdown(1L, 10, 17, LocalDate.of(2026, 8, 15));

        assertThat(breakdown.billed()).isEqualByComparingTo("0.00");
        assertThat(breakdown.unbilled()).isEqualByComparingTo("30.00");
    }

    @Test
    void cycleBreakdownTreatsPurchaseOnCurrentCutoffAsUnbilled() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("50.00", LocalDate.of(2026, 7, 5)),
                purchase("100.00", LocalDate.of(2026, 8, 5))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of());

        CreditCardAssembler.BilledBreakdown breakdown =
                assembler().cycleBreakdown(1L, 5, 17, LocalDate.of(2026, 8, 5));

        assertThat(breakdown.billed()).isEqualByComparingTo("50.00");
        assertThat(breakdown.unbilled()).isEqualByComparingTo("100.00");
    }

    @Test
    void cycleBreakdownRecomputesMissingBillingCycleFromPurchaseDate() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("50.00", LocalDate.of(2026, 7, 5), null),
                purchase("100.00", LocalDate.of(2026, 8, 5), null)));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of());

        CreditCardAssembler.BilledBreakdown breakdown =
                assembler().cycleBreakdown(1L, 5, 17, LocalDate.of(2026, 8, 5));

        assertThat(breakdown.billed()).isEqualByComparingTo("50.00");
        assertThat(breakdown.unbilled()).isEqualByComparingTo("100.00");
    }

    @Test
    void statementsGroupPurchasesByCycleWithCutoffAndDueDate() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("45000.00", LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 30)),
                purchase("100000.00", LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 30))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of());

        var result = assembler().statements(1L, 30, 17, LocalDate.of(2026, 8, 30));

        assertThat(result).hasSize(2);
        CreditCardStatementResponse first = result.get(0);
        assertThat(first.cutoffDate()).isEqualTo(LocalDate.of(2026, 7, 30));
        assertThat(first.dueDate()).isEqualTo(LocalDate.of(2026, 8, 17));
        assertThat(first.totalAmount()).isEqualByComparingTo("45000.00");
        assertThat(first.paidAmount()).isEqualByComparingTo("0.00");
        assertThat(first.remainingAmount()).isEqualByComparingTo("45000.00");

        CreditCardStatementResponse second = result.get(1);
        assertThat(second.cutoffDate()).isEqualTo(LocalDate.of(2026, 8, 30));
        assertThat(second.dueDate()).isEqualTo(LocalDate.of(2026, 9, 17));
        assertThat(second.totalAmount()).isEqualByComparingTo("100000.00");
        assertThat(second.remainingAmount()).isEqualByComparingTo("100000.00");
    }

    @Test
    void statementsApplyPartialPaymentOnlyToNextDueStatement() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("45000.00", LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 30)),
                purchase("100000.00", LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 30))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of(payment("20000.00")));

        var result = assembler().statements(1L, 30, 17, LocalDate.of(2026, 8, 30));

        assertThat(result.get(0).paidAmount()).isEqualByComparingTo("20000.00");
        assertThat(result.get(0).remainingAmount()).isEqualByComparingTo("25000.00");
        assertThat(result.get(1).paidAmount()).isEqualByComparingTo("0.00");
        assertThat(result.get(1).remainingAmount()).isEqualByComparingTo("100000.00");
    }

    @Test
    void statementsRollExcessPaymentIntoNextStatement() {
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(
                purchase("45000.00", LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 30)),
                purchase("100000.00", LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 30))));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of(payment("60000.00")));

        var result = assembler().statements(1L, 30, 17, LocalDate.of(2026, 8, 30));

        assertThat(result).hasSize(1);
        CreditCardStatementResponse remaining = result.get(0);
        assertThat(remaining.cutoffDate()).isEqualTo(LocalDate.of(2026, 8, 30));
        assertThat(remaining.paidAmount()).isEqualByComparingTo("15000.00");
        assertThat(remaining.remainingAmount()).isEqualByComparingTo("85000.00");
    }

    private CreditCardPurchase purchase(String amount) {
        return purchase(amount, LocalDate.of(2026, 8, 10));
    }

    private CreditCardPurchase purchase(String amount, LocalDate billingCycle) {
        return purchase(amount, LocalDate.of(2026, 8, 5), billingCycle);
    }

    private CreditCardPurchase purchase(String amount, LocalDate purchaseDate, LocalDate billingCycle) {
        return CreditCardPurchase.create(1L, "compra", new BigDecimal(amount), purchaseDate, billingCycle);
    }

    private CreditCardPayment payment(String amount) {
        return CreditCardPayment.create(1L, new BigDecimal(amount), LocalDate.of(2026, 8, 15));
    }
}
