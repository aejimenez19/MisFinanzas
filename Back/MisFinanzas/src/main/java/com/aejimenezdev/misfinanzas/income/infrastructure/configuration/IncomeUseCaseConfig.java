package com.aejimenezdev.misfinanzas.income.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.income.application.services.IncomeResponseAssembler;
import com.aejimenezdev.misfinanzas.income.application.usecases.CreateIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.DeleteIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.GetIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.ListIncomesUseCase;
import com.aejimenezdev.misfinanzas.income.application.usecases.UpdateIncomeUseCase;
import com.aejimenezdev.misfinanzas.income.domain.ports.IncomeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IncomeUseCaseConfig {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    public IncomeUseCaseConfig(IncomeRepository incomeRepository, CategoryRepository categoryRepository) {
        this.incomeRepository = incomeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Bean
    public IncomeResponseAssembler incomeResponseAssembler() {
        return new IncomeResponseAssembler(categoryRepository);
    }

    @Bean
    public CreateIncomeUseCase createIncomeUseCase(IncomeResponseAssembler assembler) {
        return new CreateIncomeUseCase(incomeRepository, assembler);
    }

    @Bean
    public UpdateIncomeUseCase updateIncomeUseCase(IncomeResponseAssembler assembler) {
        return new UpdateIncomeUseCase(incomeRepository, assembler);
    }

    @Bean
    public DeleteIncomeUseCase deleteIncomeUseCase() {
        return new DeleteIncomeUseCase(incomeRepository);
    }

    @Bean
    public ListIncomesUseCase listIncomesUseCase(IncomeResponseAssembler assembler) {
        return new ListIncomesUseCase(incomeRepository, assembler);
    }

    @Bean
    public GetIncomeUseCase getIncomeUseCase(IncomeResponseAssembler assembler) {
        return new GetIncomeUseCase(incomeRepository, assembler);
    }
}