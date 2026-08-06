package com.aejimenezdev.misfinanzas.category.application.dto;

import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;

public record CategoryResponse(Long id, String name, CategoryType type) {
}