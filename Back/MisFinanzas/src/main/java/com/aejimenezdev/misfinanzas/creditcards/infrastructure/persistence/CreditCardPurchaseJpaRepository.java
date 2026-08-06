package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardPurchaseJpaRepository extends JpaRepository<CreditCardPurchaseJpaEntity, Long> {

    List<CreditCardPurchaseJpaEntity> findByCreditCardIdAndDeletedFalseOrderByCreatedAtDesc(Long creditCardId);
}
