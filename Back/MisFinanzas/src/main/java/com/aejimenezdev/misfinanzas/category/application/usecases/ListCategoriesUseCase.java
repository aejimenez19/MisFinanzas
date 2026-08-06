package com.aejimenezdev.misfinanzas.category.application.usecases;

import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;

import java.util.List;

public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> execute(CategoryType type) {
        if (type == null) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByType(type);
    }
}