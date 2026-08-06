package com.aejimenezdev.misfinanzas.creditcards.domain.ports;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;

import java.util.List;

public interface CreditCardPurchaseRepository {

    CreditCardPurchase save(CreditCardPurchase purchase);

    List<CreditCardPurchase> findByCreditCardId(Long creditCardId);
}