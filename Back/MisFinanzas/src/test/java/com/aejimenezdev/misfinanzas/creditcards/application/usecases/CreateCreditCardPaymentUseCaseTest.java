package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
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
class CreateCreditCardPaymentUseCaseTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardPurchaseRepository purchaseRepository;

    @Mock
    private CreditCardPaymentRepository paymentRepository;

    private CreateCreditCardPaymentUseCase useCase() {
        CreditCardAssembler assembler = new CreditCardAssembler(purchaseRepository, paymentRepository);
        return new CreateCreditCardPaymentUseCase(creditCardRepository, paymentRepository, assembler);
    }

    @Test
    void createsPaymentAgainstOwnedCard() {
        when(creditCardRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(CreditCard.create(1L, "Banco", "Central", "1234",
                        new BigDecimal("10000.00"), 10, 15)));
        when(paymentRepository.save(any(CreditCardPayment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreditCardPaymentResponse response = useCase().execute(1L, 1L,
                new CreditCardPaymentRequest(new BigDecimal("500.00"), LocalDate.of(2026, 8, 15)));

        assertThat(response.amount()).isEqualByComparingTo("500.00");
        assertThat(response.paymentDate()).isEqualTo(LocalDate.of(2026, 8, 15));
    }

    @Test
    void throwsWhenCardMissing() {
        when(creditCardRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().execute(1L, 999L,
                new CreditCardPaymentRequest(new BigDecimal("500.00"), LocalDate.of(2026, 8, 15))))
                .isInstanceOf(CreditCardNotFoundException.class);
    }
}
