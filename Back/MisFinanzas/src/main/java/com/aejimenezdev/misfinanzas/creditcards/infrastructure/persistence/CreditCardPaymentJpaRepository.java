package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditCardPaymentJpaRepository extends JpaRepository<CreditCardPaymentJpaEntity, Long> {

    List<CreditCardPaymentJpaEntity> findByCreditCardIdAndDeletedFalse(Long creditCardId);

    @Query("""
            SELECT p FROM CreditCardPaymentJpaEntity p
            WHERE p.deleted = false
              AND p.creditCardId IN (
                  SELECT c.id FROM CreditCardJpaEntity c
                  WHERE c.userId = :userId AND c.deleted = false)
            """)
    List<CreditCardPaymentJpaEntity> findByUserIdAndDeletedFalse(@Param("userId") Long userId);
}
