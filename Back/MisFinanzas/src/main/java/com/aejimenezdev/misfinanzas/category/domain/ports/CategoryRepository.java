package com.aejimenezdev.misfinanzas.category.domain.ports;

import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findByType(CategoryType type);

    List<Category> findAll();

    Optional<Category> findById(Long id);
}