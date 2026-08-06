package com.aejimenezdev.misfinanzas.category.infrastructure.controller;

import com.aejimenezdev.misfinanzas.category.application.dto.CategoryResponse;
import com.aejimenezdev.misfinanzas.category.application.mapper.CategoryMapper;
import com.aejimenezdev.misfinanzas.category.application.usecases.ListCategoriesUseCase;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(ListCategoriesUseCase listCategoriesUseCase) {
        this.listCategoriesUseCase = listCategoriesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@RequestParam(required = false) CategoryType type) {
        List<CategoryResponse> categories = listCategoriesUseCase.execute(type)
                .stream().map(CategoryMapper::toResponse).toList();
        return ResponseEntity.ok(categories);
    }
}
