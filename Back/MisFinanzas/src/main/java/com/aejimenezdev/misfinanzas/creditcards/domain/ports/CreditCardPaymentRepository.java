package com.aejimenezdev.misfinanzas.creditcards.domain.ports;

import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;

import java.util.List;

public interface CreditCardPaymentRepository {

    CreditCardPayment save(CreditCardPayment payment);

    List<CreditCardPayment> findByCreditCardId(Long creditCardId);

    List<CreditCardPayment> findByUserId(Long userId);
}