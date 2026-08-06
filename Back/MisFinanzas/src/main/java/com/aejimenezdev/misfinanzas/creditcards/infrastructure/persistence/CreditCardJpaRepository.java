package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardJpaRepository extends JpaRepository<CreditCardJpaEntity, Long> {

    Optional<CreditCardJpaEntity> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    List<CreditCardJpaEntity> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);
}
