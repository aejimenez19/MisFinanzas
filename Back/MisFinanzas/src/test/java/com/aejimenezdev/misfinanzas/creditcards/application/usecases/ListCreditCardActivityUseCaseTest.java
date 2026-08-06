package com.aejimenezdev.misfinanzas.creditcards.application.usecases;

import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardActivityResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.services.CreditCardAssembler;
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCard;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardPayment;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCreditCardActivityUseCaseTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardPurchaseRepository purchaseRepository;

    @Mock
    private CreditCardPaymentRepository paymentRepository;

    private ListCreditCardActivityUseCase useCase() {
        CreditCardAssembler assembler = new CreditCardAssembler(purchaseRepository, paymentRepository);
        return new ListCreditCardActivityUseCase(creditCardRepository, purchaseRepository, paymentRepository, assembler);
    }

    private CreditCard card() {
        return CreditCard.create(1L, "Platinum", "Banco Central", "1234",
                new BigDecimal("10000.00"), 10, 15);
    }

    @Test
    void returnsMergedActivitySortedByDateDesc() {
        CreditCard card = card();
        when(creditCardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(card));
        when(purchaseRepository.findByCreditCardId(null)).thenReturn(List.of(
                CreditCardPurchase.create(null, "Amazon", new BigDecimal("120.50"),
                        LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 10)),
                CreditCardPurchase.create(null, "Grocery", new BigDecimal("45.00"),
                        LocalDate.of(2026, 8, 20), LocalDate.of(2026, 9, 10))
        ));
        when(paymentRepository.findByCreditCardId(null)).thenReturn(List.of(
                CreditCardPayment.create(null, new BigDecimal("500.00"), LocalDate.of(2026, 8, 18))
        ));

        List<CreditCardActivityResponse> activities = useCase().execute(1L, 1L);

        assertThat(activities).hasSize(3);
        assertThat(activities.get(0).date()).isEqualTo(LocalDate.of(2026, 8, 20));
        assertThat(activities.get(0).type()).isEqualTo("PURCHASE");
        assertThat(activities.get(0).amount()).isEqualByComparingTo("-45.00");
        assertThat(activities.get(1).type()).isEqualTo("PAYMENT");
        assertThat(activities.get(1).amount()).isEqualByComparingTo("500.00");
        assertThat(activities.get(2).type()).isEqualTo("PURCHASE");
        assertThat(activities.get(2).amount()).isEqualByComparingTo("-120.50");
    }

    @Test
    void limitsToMaxActivity() {
        CreditCard card = card();
        when(creditCardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(card));
        when(purchaseRepository.findByCreditCardId(null)).thenReturn(
                java.util.stream.IntStream.rangeClosed(1, 12)
                        .mapToObj(i -> CreditCardPurchase.create(null, "Item " + i,
                                new BigDecimal("10.00"), LocalDate.of(2026, 1, i),
                                LocalDate.of(2026, 1, 10)))
                        .toList());
        when(paymentRepository.findByCreditCardId(null)).thenReturn(List.of());

        List<CreditCardActivityResponse> activities = useCase().execute(1L, 1L);

        assertThat(activities).hasSize(10);
    }

    @Test
    void throwsWhenCardDoesNotBelongToUser() {
        when(creditCardRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().execute(1L, 999L))
                .isInstanceOf(CreditCardNotFoundException.class);
    }
}
