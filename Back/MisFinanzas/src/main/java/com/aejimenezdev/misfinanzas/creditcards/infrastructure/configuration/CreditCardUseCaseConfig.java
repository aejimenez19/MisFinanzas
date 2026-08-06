package com.aejimenezdev.misfinanzas.creditcards.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardPaymentUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardPurchaseUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.DeleteCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.GetCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.ListCreditCardActivityUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.ListCreditCardsUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.UpdateCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreditCardUseCaseConfig {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardPurchaseRepository purchaseRepository;
    private final CreditCardPaymentRepository paymentRepository;

    public CreditCardUseCaseConfig(CreditCardRepository creditCardRepository,
                                   CreditCardPurchaseRepository purchaseRepository,
                                   CreditCardPaymentRepository paymentRepository) {
        this.creditCardRepository = creditCardRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Bean
    public CreditCardAssembler creditCardAssembler() {
        return new CreditCardAssembler(purchaseRepository, paymentRepository);
    }

    @Bean
    public CreateCreditCardUseCase createCreditCardUseCase(CreditCardAssembler assembler) {
        return new CreateCreditCardUseCase(creditCardRepository, assembler);
    }

    @Bean
    public UpdateCreditCardUseCase updateCreditCardUseCase(CreditCardAssembler assembler) {
        return new UpdateCreditCardUseCase(creditCardRepository, assembler);
    }

    @Bean
    public DeleteCreditCardUseCase deleteCreditCardUseCase() {
        return new DeleteCreditCardUseCase(creditCardRepository);
    }

    @Bean
    public ListCreditCardsUseCase listCreditCardsUseCase(CreditCardAssembler assembler) {
        return new ListCreditCardsUseCase(creditCardRepository, assembler);
    }

    @Bean
    public GetCreditCardUseCase getCreditCardUseCase(CreditCardAssembler assembler) {
        return new GetCreditCardUseCase(creditCardRepository, assembler);
    }

    @Bean
    public ListCreditCardActivityUseCase listCreditCardActivityUseCase(CreditCardAssembler assembler) {
        return new ListCreditCardActivityUseCase(creditCardRepository, purchaseRepository, paymentRepository, assembler);
    }

    @Bean
    public CreateCreditCardPurchaseUseCase createCreditCardPurchaseUseCase(CreditCardAssembler assembler) {
        return new CreateCreditCardPurchaseUseCase(creditCardRepository, purchaseRepository, assembler);
    }

    @Bean
    public CreateCreditCardPaymentUseCase createCreditCardPaymentUseCase(CreditCardAssembler assembler) {
        return new CreateCreditCardPaymentUseCase(creditCardRepository, paymentRepository, assembler);
    }
}
