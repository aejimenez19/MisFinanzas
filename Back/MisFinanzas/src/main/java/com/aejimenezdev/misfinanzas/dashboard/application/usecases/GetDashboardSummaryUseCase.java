package com.aejimenezdev.misfinanzas.dashboard.application.usecases;

import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.CardBalanceView;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.DashboardSummary;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.MonthlyFlow;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.MovementType;
import com.aejimenezdev.misfinanzas.dashboard.domain.model.RecentMovement;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GetDashboardSummaryUseCase {

    private static final int RECENT_MOVEMENTS_LIMIT = 6;
    private static final int CASHFLOW_MONTHS = 12;

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardPaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;
    private final CreditCardAssembler creditCardAssembler;

    private final Clock clock;

    public GetDashboardSummaryUseCase(IncomeRepository incomeRepository,
                                      ExpenseRepository expenseRepository,
                                      CreditCardRepository creditCardRepository,
                                      CreditCardPaymentRepository paymentRepository,
                                      CategoryRepository categoryRepository,
                                      CreditCardAssembler creditCardAssembler,
                                      Clock clock) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.creditCardRepository = creditCardRepository;
        this.paymentRepository = paymentRepository;
        this.categoryRepository = categoryRepository;
        this.creditCardAssembler = creditCardAssembler;
        this.clock = clock;
    }

    public DashboardSummary execute(Long userId) {
        LocalDate today = LocalDate.now(clock);
        LocalDate monthStart = today.withDayOfMonth(1);

        List<Income> incomes = incomeRepository.findByUserId(userId);
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        List<CreditCard> cards = creditCardRepository.findByUserId(userId);
        List<CreditCardPayment> payments = paymentRepository.findByUserId(userId);

        BigDecimal availableBalance = sumIncomes(incomes)
                .subtract(sumExpenses(expenses))
                .subtract(sumPayments(payments));

        BigDecimal monthIncomes = sumIncomes(incomes, monthStart);
        BigDecimal monthExpenses = sumExpenses(expenses, monthStart)
                .add(sumPayments(payments, monthStart));

        List<CardBalanceView> cardViews = cards.stream()
                .map(card -> toCardView(card, today))
                .toList();

        NextPayment nextPayment = resolveNextPayment(cardViews, today);

        List<RecentMovement> recentMovements = buildRecentMovements(incomes, expenses);

        return new DashboardSummary(
                availableBalance,
                monthIncomes,
                monthExpenses,
                today,
                buildCashflow(incomes, expenses, payments, today),
                cardViews,
                nextPayment.amount(),
                nextPayment.dueDate(),
                nextPayment.daysUntilDue(),
                recentMovements);
    }

    private CardBalanceView toCardView(CreditCard card, LocalDate today) {
        BigDecimal pending = creditCardAssembler.pendingBalance(card.id());
        BigDecimal billed = creditCardAssembler.billedBalance(card.id(), card.cutoffDay(), card.paymentDay(), today);
        LocalDate due = creditCardAssembler.nextPaymentDate(card.paymentDay(), today);
        return new CardBalanceView(
                card.id(),
                card.name(),
                card.bank(),
                pending,
                billed,
                card.creditLimit(),
                creditCardAssembler.limitUsagePercent(pending, card.creditLimit()),
                card.paymentDay(),
                due);
    }

    private NextPayment resolveNextPayment(List<CardBalanceView> cardViews, LocalDate today) {
        Optional<CardBalanceView> next = cardViews.stream()
                .filter(view -> view.billedBalance().compareTo(BigDecimal.ZERO) > 0)
                .min(Comparator.comparing(CardBalanceView::nextPaymentDate));

        if (next.isEmpty()) {
            return new NextPayment(BigDecimal.ZERO, null, 0);
        }
        CardBalanceView view = next.get();
        return new NextPayment(view.billedBalance(), view.nextPaymentDate(),
                Math.max(0, ChronoUnit.DAYS.between(today, view.nextPaymentDate())));
    }

    private List<MonthlyFlow> buildCashflow(List<Income> incomes, List<Expense> expenses,
                                            List<CreditCardPayment> payments, LocalDate today) {
        YearMonth start = YearMonth.from(today).minusMonths(CASHFLOW_MONTHS - 1);
        Map<YearMonth, MonthlyFlow> byMonth = new TreeMap<>();
        for (int i = 0; i < CASHFLOW_MONTHS; i++) {
            YearMonth month = start.plusMonths(i);
            byMonth.put(month, MonthlyFlow.empty(month.toString()));
        }

        for (Income income : incomes) {
            YearMonth month = YearMonth.from(income.movementDate());
            MonthlyFlow flow = byMonth.get(month);
            if (flow != null) {
                byMonth.put(month, new MonthlyFlow(month.toString(),
                        flow.incomes().add(income.amount()), flow.expenses()));
            }
        }
        for (Expense expense : expenses) {
            YearMonth month = YearMonth.from(expense.movementDate());
            MonthlyFlow flow = byMonth.get(month);
            if (flow != null) {
                byMonth.put(month, new MonthlyFlow(month.toString(),
                        flow.incomes(), flow.expenses().add(expense.amount())));
            }
        }
        for (CreditCardPayment payment : payments) {
            YearMonth month = YearMonth.from(payment.paymentDate());
            MonthlyFlow flow = byMonth.get(month);
            if (flow != null) {
                byMonth.put(month, new MonthlyFlow(month.toString(),
                        flow.incomes(), flow.expenses().add(payment.amount())));
            }
        }
        return new ArrayList<>(byMonth.values());
    }

    private List<RecentMovement> buildRecentMovements(List<Income> incomes, List<Expense> expenses) {
        Map<Long, String> incomeNames = categoryRepository.findByType(CategoryType.INCOME).stream()
                .collect(Collectors.toMap(Category::id, Category::name));
        Map<Long, String> expenseNames = categoryRepository.findByType(CategoryType.EXPENSE).stream()
                .collect(Collectors.toMap(Category::id, Category::name));

        List<RecentMovement> movements = new ArrayList<>();
        for (Income income : incomes) {
            movements.add(new RecentMovement(income.id(), MovementType.INCOME, income.description(),
                    income.amount(), incomeNames.get(income.categoryId()), income.movementDate()));
        }
        for (Expense expense : expenses) {
            movements.add(new RecentMovement(expense.id(), MovementType.EXPENSE, expense.description(),
                    expense.amount(), expenseNames.get(expense.categoryId()), expense.movementDate()));
        }

        movements.sort(Comparator.comparing(RecentMovement::movementDate).reversed());
        return movements.stream().limit(RECENT_MOVEMENTS_LIMIT).toList();
    }

    private BigDecimal sumIncomes(List<Income> incomes) {
        return incomes.stream().map(Income::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumExpenses(List<Expense> expenses) {
        return expenses.stream().map(Expense::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPayments(List<CreditCardPayment> payments) {
        return payments.stream().map(CreditCardPayment::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPayments(List<CreditCardPayment> payments, LocalDate from) {
        return payments.stream()
                .filter(payment -> !payment.paymentDate().isBefore(from))
                .map(CreditCardPayment::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumIncomes(List<Income> incomes, LocalDate from) {
        return incomes.stream()
                .filter(income -> !income.movementDate().isBefore(from))
                .map(Income::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumExpenses(List<Expense> expenses, LocalDate from) {
        return expenses.stream()
                .filter(expense -> !expense.movementDate().isBefore(from))
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record NextPayment(BigDecimal amount, LocalDate dueDate, long daysUntilDue) {
    }
}
