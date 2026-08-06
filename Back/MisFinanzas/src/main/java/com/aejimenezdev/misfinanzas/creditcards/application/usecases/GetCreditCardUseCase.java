package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

import java.time.LocalDate;

public class GetCreditCardUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardAssembler assembler;

    public GetCreditCardUseCase(CreditCardRepository creditCardRepository, CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.assembler = assembler;
    }

    public CreditCardResponse execute(Long userId, Long id) {
        CreditCard card = creditCardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));
        return assembler.toCardResponse(card, LocalDate.now());
    }
}
