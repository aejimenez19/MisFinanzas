package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CreditCardRepositoryAdapter implements CreditCardRepository {

    private final CreditCardJpaRepository jpaRepository;

    public CreditCardRepositoryAdapter(CreditCardJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CreditCard save(CreditCard creditCard) {
        return toDomain(jpaRepository.save(toEntity(creditCard)));
    }

    @Override
    public Optional<CreditCard> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserIdAndDeletedFalse(id, userId).map(this::toDomain);
    }

    @Override
    public List<CreditCard> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDomain).toList();
    }

    private CreditCard toDomain(CreditCardJpaEntity entity) {
        return new CreditCard(entity.getId(), entity.getUserId(), entity.getName(), entity.getBank(),
                entity.getLastFourDigits(), entity.getCreditLimit(), entity.getCutoffDay().intValue(),
                entity.getPaymentDay().intValue(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt(), entity.isDeleted());
    }

    private CreditCardJpaEntity toEntity(CreditCard card) {
        return new CreditCardJpaEntity(card.id(), card.userId(), card.name(), card.bank(),
                card.lastFourDigits(), card.creditLimit(), (short) card.cutoffDay(), (short) card.paymentDay(), card.status(),
                card.createdAt(), card.updatedAt(), card.deleted());
    }
}
