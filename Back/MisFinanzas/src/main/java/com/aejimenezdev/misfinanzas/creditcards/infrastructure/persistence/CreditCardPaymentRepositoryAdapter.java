package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreditCardPaymentRepositoryAdapter implements CreditCardPaymentRepository {

    private final CreditCardPaymentJpaRepository jpaRepository;

    public CreditCardPaymentRepositoryAdapter(CreditCardPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CreditCardPayment save(CreditCardPayment payment) {
        return toDomain(jpaRepository.save(toEntity(payment)));
    }

    @Override
    public List<CreditCardPayment> findByCreditCardId(Long creditCardId) {
        return jpaRepository.findByCreditCardIdAndDeletedFalse(creditCardId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CreditCardPayment> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalse(userId)
                .stream().map(this::toDomain).toList();
    }

    private CreditCardPayment toDomain(CreditCardPaymentJpaEntity entity) {
        return new CreditCardPayment(entity.getId(), entity.getCreditCardId(), entity.getAmount(),
                entity.getPaymentDate(), entity.getCreatedAt(), entity.isDeleted());
    }

    private CreditCardPaymentJpaEntity toEntity(CreditCardPayment payment) {
        return new CreditCardPaymentJpaEntity(payment.id(), payment.creditCardId(), payment.amount(),
                payment.paymentDate(), payment.createdAt(), payment.deleted());
    }
}
