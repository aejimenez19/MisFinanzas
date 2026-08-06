package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

public class DeleteCreditCardUseCase {

    private final CreditCardRepository creditCardRepository;

    public DeleteCreditCardUseCase(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public void execute(Long userId, Long id) {
        CreditCard existing = creditCardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));
        creditCardRepository.save(existing.markDeleted());
    }
}
