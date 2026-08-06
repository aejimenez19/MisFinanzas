package com.aejimenezdev.misfinanzas.category.application.mapper;

import com.aejimenezdev.misfinanzas.category.application.dto.CategoryResponse;
import com.aejimenezdev.misfinanzas.category.domain.model.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.type());
    }
}