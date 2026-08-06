package com.aejimenezdev.misfinanzas.category.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.category.application.usecases.ListCategoriesUseCase;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryRepository categoryRepository;

    public CategoryUseCaseConfig(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new ListCategoriesUseCase(categoryRepository);
    }
}
