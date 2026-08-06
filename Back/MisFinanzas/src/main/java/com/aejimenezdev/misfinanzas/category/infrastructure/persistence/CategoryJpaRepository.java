package com.aejimenezdev.misfinanzas.category.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    List<CategoryJpaEntity> findByTypeOrderByNameAsc(CategoryType type);
}
