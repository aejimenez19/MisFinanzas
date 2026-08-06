package com.aejimenezdev.misfinanzas.expense.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.expense.application.services.ExpenseResponseAssembler;
import com.aejimenezdev.misfinanzas.expense.application.usecases.CreateExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.DeleteExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.GetExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.ListExpensesUseCase;
import com.aejimenezdev.misfinanzas.expense.application.usecases.UpdateExpenseUseCase;
import com.aejimenezdev.misfinanzas.expense.domain.ports.ExpenseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpenseUseCaseConfig {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseUseCaseConfig(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Bean
    public ExpenseResponseAssembler expenseResponseAssembler() {
        return new ExpenseResponseAssembler(categoryRepository);
    }

    @Bean
    public CreateExpenseUseCase createExpenseUseCase(ExpenseResponseAssembler assembler) {
        return new CreateExpenseUseCase(expenseRepository, assembler);
    }

    @Bean
    public UpdateExpenseUseCase updateExpenseUseCase(ExpenseResponseAssembler assembler) {
        return new UpdateExpenseUseCase(expenseRepository, assembler);
    }

    @Bean
    public DeleteExpenseUseCase deleteExpenseUseCase() {
        return new DeleteExpenseUseCase(expenseRepository);
    }

    @Bean
    public ListExpensesUseCase listExpensesUseCase(ExpenseResponseAssembler assembler) {
        return new ListExpensesUseCase(expenseRepository, assembler);
    }

    @Bean
    public GetExpenseUseCase getExpenseUseCase(ExpenseResponseAssembler assembler) {
        return new GetExpenseUseCase(expenseRepository, assembler);
    }
}