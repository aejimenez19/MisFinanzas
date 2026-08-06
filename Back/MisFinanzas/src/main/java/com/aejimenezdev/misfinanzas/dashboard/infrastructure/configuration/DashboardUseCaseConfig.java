package com.aejimenezdev.misfinanzas.dashboard.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import com.aejimenezdev.misfinanzas.dashboard.application.services.DashboardAssembler;
import com.aejimenezdev.misfinanzas.dashboard.application.usecases.GetDashboardSummaryUseCase;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DashboardUseCaseConfig {

    @Bean
    public DashboardAssembler dashboardAssembler() {
        return new DashboardAssembler();
    }

    @Bean
    public GetDashboardSummaryUseCase getDashboardSummaryUseCase(IncomeRepository incomeRepository,
                                                                 ExpenseRepository expenseRepository,
                                                                 CreditCardRepository creditCardRepository,
                                                                 CreditCardPaymentRepository paymentRepository,
                                                                 CategoryRepository categoryRepository,
                                                                 CreditCardAssembler creditCardAssembler) {
        return new GetDashboardSummaryUseCase(incomeRepository, expenseRepository, creditCardRepository,
                paymentRepository, categoryRepository, creditCardAssembler, Clock.systemDefaultZone());
    }
}
