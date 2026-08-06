package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

import java.time.LocalDate;

public class CreateCreditCardUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardAssembler assembler;

    public CreateCreditCardUseCase(CreditCardRepository creditCardRepository, CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.assembler = assembler;
    }

    public CreditCardResponse execute(Long userId, CreditCardRequest request) {
        CreditCard card = CreditCard.create(userId, request.name(), request.bank(),
                request.lastFourDigits(), request.creditLimit(), request.cutoffDay(), request.paymentDay());
        CreditCard saved = creditCardRepository.save(card);
        return assembler.toCardResponse(saved, LocalDate.now());
    }
}
