package com.aejimenezdev.misfinanzas.dashboard.application.usecases;

import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardStatus;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.DashboardSummary;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardSummaryUseCaseTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 8, 15);
    private static final Clock CLOCK = Clock.fixed(
            TODAY.atStartOfDay(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardPaymentRepository paymentRepository;

    @Mock
    private CreditCardPurchaseRepository purchaseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private GetDashboardSummaryUseCase useCase() {
        CreditCardAssembler assembler = new CreditCardAssembler(purchaseRepository, paymentRepository);
        return new GetDashboardSummaryUseCase(incomeRepository, expenseRepository, creditCardRepository,
                paymentRepository, categoryRepository, assembler, CLOCK);
    }

    @Test
    void availableBalanceIsIncomesMinusExpensesMinusPayments() {
        when(incomeRepository.findByUserId(1L)).thenReturn(List.of(income("5000.00", LocalDate.of(2026, 7, 1))));
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(expense("1000.00", LocalDate.of(2026, 7, 5))));
        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of());
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(payment("500.00", LocalDate.of(2026, 7, 15))));

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.availableBalance()).isEqualByComparingTo("3500.00");
    }

    @Test
    void monthlyFiguresOnlyCountCurrentMonth() {
        when(incomeRepository.findByUserId(1L)).thenReturn(List.of(
                income("2000.00", TODAY.withDayOfMonth(10)),
                income("1500.00", LocalDate.of(2026, 7, 20))));
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(
                expense("500.00", TODAY.withDayOfMonth(2))));
        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of());
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of());

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.monthIncomes()).isEqualByComparingTo("2000.00");
        assertThat(summary.monthExpenses()).isEqualByComparingTo("500.00");
    }

    @Test
    void cashflowContainsTwelveMonthsAscendingAndSumsByMonth() {
        when(incomeRepository.findByUserId(1L)).thenReturn(List.of(
                income("100.00", LocalDate.of(2026, 8, 1))));
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(
                expense("30.00", LocalDate.of(2026, 8, 5)),
                expense("20.00", LocalDate.of(2026, 7, 10))));
        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of());
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of());

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.cashflow()).hasSize(12);
        assertThat(summary.cashflow().get(0).month()).isEqualTo("2025-09");
        var august = summary.cashflow().stream()
                .filter(flow -> flow.month().equals("2026-08"))
                .findFirst().orElseThrow();
        assertThat(august.incomes()).isEqualByComparingTo("100.00");
        assertThat(august.expenses()).isEqualByComparingTo("30.00");
    }

    @Test
    void nextPaymentComesFromCardWithSoonestDueDateAndPositivePending() {
        Instant now = Instant.now();
        CreditCard platinum = new CreditCard(1L, 1L, "Platinum", "Banco Central", "1234",
                new BigDecimal("10000.00"), 10, 15, CreditCardStatus.ACTIVE, now, now, false);
        CreditCard rewards = new CreditCard(2L, 1L, "Rewards", "Banco Central", "5678",
                new BigDecimal("7500.00"), 5, 22, CreditCardStatus.ACTIVE, now, now, false);

        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of(platinum, rewards));
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of());
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of(purchase("3420.00")));
        when(purchaseRepository.findByCreditCardId(2L)).thenReturn(List.of(purchase("1150.00")));
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of());
        when(paymentRepository.findByCreditCardId(2L)).thenReturn(List.of());

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.cards()).hasSize(2);
        assertThat(summary.nextPaymentAmount()).isEqualByComparingTo("3420.00");
        assertThat(summary.nextPaymentDate()).isEqualTo(LocalDate.of(2026, 8, 15));
        assertThat(summary.daysUntilDue()).isEqualTo(0);
    }

    @Test
    void nextPaymentIsZeroWhenNoCardHasPending() {
        Instant now = Instant.now();
        CreditCard platinum = new CreditCard(1L, 1L, "Platinum", "Banco Central", "1234",
                new BigDecimal("10000.00"), 10, 15, CreditCardStatus.ACTIVE, now, now, false);

        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of(platinum));
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of());
        when(purchaseRepository.findByCreditCardId(1L)).thenReturn(List.of());
        when(paymentRepository.findByCreditCardId(1L)).thenReturn(List.of());

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.nextPaymentAmount()).isEqualByComparingTo("0.00");
        assertThat(summary.nextPaymentDate()).isNull();
    }

    @Test
    void creditCardPaymentsCountAsMonthExpensesAndInCashflow() {
        when(incomeRepository.findByUserId(1L)).thenReturn(List.of());
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(
                expense("500.00", TODAY.withDayOfMonth(2))));
        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of());
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(
                payment("300.00", TODAY.withDayOfMonth(10)),
                payment("200.00", LocalDate.of(2026, 7, 20))));

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.monthExpenses()).isEqualByComparingTo("800.00");
        var august = summary.cashflow().stream()
                .filter(flow -> flow.month().equals("2026-08"))
                .findFirst().orElseThrow();
        assertThat(august.expenses()).isEqualByComparingTo("800.00");
    }

    @Test
    void recentMovementsAreMergedSortedDescendingWithCategoryNames() {
        when(incomeRepository.findByUserId(1L)).thenReturn(List.of(
                income("2000.00", LocalDate.of(2026, 8, 1))));
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(
                expense("50.00", LocalDate.of(2026, 8, 10))));
        when(creditCardRepository.findByUserId(1L)).thenReturn(List.of());
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of());
        when(categoryRepository.findByType(CategoryType.INCOME))
                .thenReturn(List.of(new Category(1L, "Salario", CategoryType.INCOME)));
        when(categoryRepository.findByType(CategoryType.EXPENSE))
                .thenReturn(List.of(new Category(2L, "Transporte", CategoryType.EXPENSE)));

        DashboardSummary summary = useCase().execute(1L);

        assertThat(summary.recentMovements()).hasSize(2);
        assertThat(summary.recentMovements().get(0).categoryName()).isEqualTo("Transporte");
        assertThat(summary.recentMovements().get(1).categoryName()).isEqualTo("Salario");
    }

    private Income income(String amount, LocalDate date) {
        Instant now = Instant.now();
        return new Income(1L, 1L, "income", new BigDecimal(amount), 1L, date, now, now, false);
    }

    private Expense expense(String amount, LocalDate date) {
        Instant now = Instant.now();
        return new Expense(2L, 1L, "expense", new BigDecimal(amount), 2L, date, now, now, false);
    }

    private CreditCardPayment payment(String amount, LocalDate date) {
        return CreditCardPayment.create(1L, new BigDecimal(amount), date);
    }

    private CreditCardPurchase purchase(String amount) {
        return CreditCardPurchase.create(1L, "purchase", new BigDecimal(amount),
                LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 10));
    }
}
