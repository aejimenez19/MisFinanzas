package com.aejimenezdev.misfinanzas.income.application.services;

import com.aejimenezdev.misfinanzas.category.domain.exceptions.CategoryNotFoundException;
import com.aejimenezdev.misfinanzas.category.domain.exceptions.InvalidCategoryException;
import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.income.application.dto.IncomeResponse;
import com.aejimenezdev.misfinanzas.income.domain.model.Income;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IncomeResponseAssembler {

    private final CategoryRepository categoryRepository;

    public IncomeResponseAssembler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category requireIncomeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if (category.type() != CategoryType.INCOME) {
            throw new InvalidCategoryException("Category is not valid for incomes");
        }
        return category;
    }

    public IncomeResponse toResponse(Income income) {
        return toResponse(income, categoryName(income.categoryId()));
    }

    public List<IncomeResponse> toResponseList(List<Income> incomes) {
        Map<Long, String> names = categoryRepository.findByType(CategoryType.INCOME).stream()
                .collect(Collectors.toMap(Category::id, Category::name));
        return incomes.stream()
                .map(income -> toResponse(income, names.getOrDefault(income.categoryId(), null)))
                .toList();
    }

    private IncomeResponse toResponse(Income income, String categoryName) {
        return new IncomeResponse(income.id(), income.description(), income.amount(),
                income.categoryId(), categoryName, income.movementDate(),
                income.createdAt(), income.updatedAt());
    }

    private String categoryName(Long categoryId) {
        return categoryRepository.findById(categoryId).map(Category::name).orElse(null);
    }
}
