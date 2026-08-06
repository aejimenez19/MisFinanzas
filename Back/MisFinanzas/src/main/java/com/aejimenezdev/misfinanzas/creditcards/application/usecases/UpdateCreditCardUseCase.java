package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

import java.time.LocalDate;

public class UpdateCreditCardUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardAssembler assembler;

    public UpdateCreditCardUseCase(CreditCardRepository creditCardRepository, CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.assembler = assembler;
    }

    public CreditCardResponse execute(Long userId, Long id, CreditCardRequest request) {
        CreditCard existing = creditCardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));
        CreditCard updated = existing.update(request.name(), request.bank(), request.lastFourDigits(),
                request.creditLimit(), request.cutoffDay(), request.paymentDay(),
                request.status() == null ? existing.status() : request.status());
        return assembler.toCardResponse(creditCardRepository.save(updated), LocalDate.now());
    }
}
