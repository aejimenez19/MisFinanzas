package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;

public class CreateCreditCardPaymentUseCase {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardPaymentRepository paymentRepository;
    private final CreditCardAssembler assembler;

    public CreateCreditCardPaymentUseCase(CreditCardRepository creditCardRepository,
                                          CreditCardPaymentRepository paymentRepository,
                                          CreditCardAssembler assembler) {
        this.creditCardRepository = creditCardRepository;
        this.paymentRepository = paymentRepository;
        this.assembler = assembler;
    }

    public CreditCardPaymentResponse execute(Long userId, Long creditCardId, CreditCardPaymentRequest request) {
        CreditCard card = creditCardRepository.findByIdAndUserId(creditCardId, userId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found"));
        CreditCardPayment payment = CreditCardPayment.create(
                card.id(), request.amount(), request.paymentDate());
        return assembler.toPaymentResponse(paymentRepository.save(payment));
    }
}
