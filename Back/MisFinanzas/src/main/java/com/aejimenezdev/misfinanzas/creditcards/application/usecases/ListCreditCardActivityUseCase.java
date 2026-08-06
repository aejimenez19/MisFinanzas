package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardActivityResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListCreditCardActivityUseCase {

    private static final int MAX_ACTIVITY = 10;

    private final CreditCardRepository creditCardRepository;
    private final CreditCardPurchaseRepository purchaseRepository;
    private final CreditCardPaymentRepository paymentRepository;
    private final CreditCardAssembler assembler;

    public ListCreditCardActivityUseCase(CreditCardRepository creditCardRepository,
                                         CreditCardPurchaseRepository purchaseRepository,
                                         CreditCardPaymentRepository paymentRepository,
                                         CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.assembler = assembler;
    }

    public List<CreditCardActivityResponse> execute(Long userId, Long creditCardId) {
        CreditCard card = creditCardRepository.findByIdAndUserId(creditCardId, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));

        List<CreditCardActivityResponse> activities = new ArrayList<>();
        activities.addAll(purchaseRepository.findByCreditCardId(card.id()).stream()
                .map(assembler::toPurchaseResponse)
                .map(CreditCardActivityResponse::purchase)
                .toList());
        activities.addAll(paymentRepository.findByCreditCardId(card.id()).stream()
                .map(assembler::toPaymentResponse)
                .map(CreditCardActivityResponse::payment)
                .toList());

        return activities.stream()
                .sorted(Comparator.comparing(CreditCardActivityResponse::date).reversed())
                .limit(MAX_ACTIVITY)
                .toList();
    }
}