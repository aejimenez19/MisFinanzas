package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

import java.util.List;

public class ListCreditCardsUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardAssembler assembler;

    public ListCreditCardsUseCase(CreditCardRepository creditCardRepository, CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.assembler = assembler;
    }

    public List<CreditCardResponse> execute(Long userId) {
        return assembler.toCardResponseList(creditCardRepository.findByUserId(userId));
    }
}
