package com.aejimenezdev.misfinanzas.expense.application.services;

import com.aejimenezdev.misfinanzas.category.domain.exceptions.CategoryNotFoundException;
import com.aejimenezdev.misfinanzas.category.domain.exceptions.InvalidCategoryException;
import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import com.aejimenezdev.misfinanzas.category.domain.ports.CategoryRepository;
import com.aejimenezdev.misfinanzas.expense.application.dto.ExpenseResponse;
import com.aejimenezdev.misfinanzas.expense.domain.model.Expense;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseResponseAssembler {

    private final CategoryRepository categoryRepository;

    public ExpenseResponseAssembler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category requireExpenseCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if (category.type() != CategoryType.EXPENSE) {
            throw new InvalidCategoryException("Category is not valid for expenses");
        }
        return category;
    }

    public ExpenseResponse toResponse(Expense expense) {
        return toResponse(expense, categoryName(expense.categoryId()));
    }

    public List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        Map<Long, String> names = categoryRepository.findByType(CategoryType.EXPENSE).stream()
                .collect(Collectors.toMap(Category::id, Category::name));
        return expenses.stream()
                .map(expense -> toResponse(expense, names.getOrDefault(expense.categoryId(), null)))
                .toList();
    }

    private ExpenseResponse toResponse(Expense expense, String categoryName) {
        return new ExpenseResponse(expense.id(), expense.description(), expense.amount(),
                expense.categoryId(), categoryName, expense.movementDate(),
                expense.createdAt(), expense.updatedAt());
    }

    private String categoryName(Long categoryId) {
        return categoryRepository.findById(categoryId).map(Category::name).orElse(null);
    }
}