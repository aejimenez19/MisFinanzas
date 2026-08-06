package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPurchase;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPaymentRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardPurchaseRepository;
import com.aejimenezdev.misfinanzas.creditcards.domain.ports.CreditCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCreditCardPurchaseUseCaseTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardPurchaseRepository purchaseRepository;

    @Mock
    private CreditCardPaymentRepository paymentRepository;

    private CreateCreditCardPurchaseUseCase useCase() {
        CreditCardAssembler assembler = new CreditCardAssembler(purchaseRepository, paymentRepository);
        return new CreateCreditCardPurchaseUseCase(creditCardRepository, purchaseRepository, assembler);
    }

    private CreditCard card() {
        return CreditCard.create(1L, "Platinum", "Banco Central", "1234",
                new BigDecimal("10000.00"), 10, 15);
    }

    @Test
    void createsPurchaseWithBillingCycleBeforeCutoff() {
        CreditCard card = card();
        when(creditCardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(card));
        when(purchaseRepository.save(any(CreditCardPurchase.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreditCardPurchaseResponse response = useCase().execute(1L, 1L,
                new CreditCardPurchaseRequest("Amazon", new BigDecimal("120.50"), LocalDate.of(2026, 8, 5)));

        assertThat(response.description()).isEqualTo("Amazon");
        assertThat(response.amount()).isEqualByComparingTo("120.50");
        assertThat(response.billingCycle()).isEqualTo(LocalDate.of(2026, 8, 10));
    }

    @Test
    void createsPurchaseWithBillingCycleAfterCutoff() {
        CreditCard card = card();
        when(creditCardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(card));
        when(purchaseRepository.save(any(CreditCardPurchase.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreditCardPurchaseResponse response = useCase().execute(1L, 1L,
                new CreditCardPurchaseRequest("Amazon", new BigDecimal("120.00"), LocalDate.of(2026, 8, 20)));

        assertThat(response.billingCycle()).isEqualTo(LocalDate.of(2026, 9, 10));
    }

    @Test
    void throwsWhenCardDoesNotBelongToUser() {
        when(creditCardRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().execute(1L, 999L,
                new CreditCardPurchaseRequest("Amazon", new BigDecimal("10.00"), LocalDate.of(2026, 8, 5))))
                .isInstanceOf(CreditCardNotFoundException.class);
    }
}
