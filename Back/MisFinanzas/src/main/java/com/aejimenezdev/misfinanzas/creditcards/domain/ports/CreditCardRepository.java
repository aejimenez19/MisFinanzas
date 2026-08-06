package com.aejimenezdev.misfinanzas.creditcards.domain.ports;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository {

    CreditCard save(CreditCard creditCard);

    Optional<CreditCard> findByIdAndUserId(Long id, Long userId);

    List<CreditCard> findByUserId(Long userId);
}