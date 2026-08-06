package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

public class CreateCreditCardPurchaseUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardPurchaseRepository purchaseRepository;
    private final CreditCardAssembler assembler;

    public CreateCreditCardPurchaseUseCase(CreditCardRepository creditCardRepository,
                                           CreditCardPurchaseRepository purchaseRepository,
                                           CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.purchaseRepository = purchaseRepository;
        this.assembler = assembler;
    }

    public CreditCardPurchaseResponse execute(Long userId, Long creditCardId, CreditCardPurchaseRequest request) {
        CreditCard card = creditCardRepository.findByIdAndUserId(creditCardId, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));
        var billingCycle = assembler.billingCycle(request.purchaseDate(), card.cutoffDay());
        CreditCardPurchase purchase = CreditCardPurchase.create(
                card.id(), request.description(), request.amount(), request.purchaseDate(), billingCycle);
        return assembler.toPurchaseResponse(purchaseRepository.save(purchase));
    }
}
