package com.aejimenezdev.misfinanzas.creditcards.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CreditCardPurchaseRepositoryAdapter implements CreditCardPurchaseRepository {

    private final CreditCardPurchaseJpaRepository jpaRepository;

    public CreditCardPurchaseRepositoryAdapter(CreditCardPurchaseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CreditCardPurchase save(CreditCardPurchase purchase) {
        return toDomain(jpaRepository.save(toEntity(purchase)));
    }

    @Override
    public List<CreditCardPurchase> findByCreditCardId(Long creditCardId) {
        return jpaRepository.findByCreditCardIdAndDeletedFalseOrderByCreatedAtDesc(creditCardId)
                .stream().map(this::toDomain).toList();
    }

    private CreditCardPurchase toDomain(CreditCardPurchaseJpaEntity entity) {
        return new CreditCardPurchase(entity.getId(), entity.getCreditCardId(), entity.getDescription(),
                entity.getAmount(), entity.getPurchaseDate(), entity.getBillingCycle(),
                entity.getCreatedAt(), entity.isDeleted());
    }

    private CreditCardPurchaseJpaEntity toEntity(CreditCardPurchase purchase) {
        return new CreditCardPurchaseJpaEntity(purchase.id(), purchase.creditCardId(), purchase.description(),
                purchase.amount(), purchase.purchaseDate(), purchase.billingCycle(),
                purchase.createdAt(), purchase.deleted());
    }
}
