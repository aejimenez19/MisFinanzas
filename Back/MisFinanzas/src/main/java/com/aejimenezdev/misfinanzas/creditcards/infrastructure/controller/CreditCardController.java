package com.aejimenezdev.misfinanzas.creditcards.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardActivityResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPaymentResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardPurchaseResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardRequest;
import com.aejimenezdev.misfinanzas.creditcards.application.dto.CreditCardResponse;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardPaymentUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardPurchaseUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.CreateCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.DeleteCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.GetCreditCardUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.ListCreditCardActivityUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.ListCreditCardsUseCase;
import com.aejimenezdev.misfinanzas.creditcards.application.usecases.UpdateCreditCardUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private final CreateCreditCardUseCase createCreditCardUseCase;
    private final UpdateCreditCardUseCase updateCreditCardUseCase;
    private final DeleteCreditCardUseCase deleteCreditCardUseCase;
    private final ListCreditCardsUseCase listCreditCardsUseCase;
    private final GetCreditCardUseCase getCreditCardUseCase;
    private final ListCreditCardActivityUseCase listActivityUseCase;
    private final CreateCreditCardPurchaseUseCase createPurchaseUseCase;
    private final CreateCreditCardPaymentUseCase createPaymentUseCase;

    public CreditCardController(CreateCreditCardUseCase createCreditCardUseCase,
                                UpdateCreditCardUseCase updateCreditCardUseCase,
                                DeleteCreditCardUseCase deleteCreditCardUseCase,
                                ListCreditCardsUseCase listCreditCardsUseCase,
                                GetCreditCardUseCase getCreditCardUseCase,
                                ListCreditCardActivityUseCase listActivityUseCase,
                                CreateCreditCardPurchaseUseCase createPurchaseUseCase,
                                CreateCreditCardPaymentUseCase createPaymentUseCase) {
        this.createCreditCardUseCase = createCreditCardUseCase;
        this.updateCreditCardUseCase = updateCreditCardUseCase;
        this.deleteCreditCardUseCase = deleteCreditCardUseCase;
        this.listCreditCardsUseCase = listCreditCardsUseCase;
        this.getCreditCardUseCase = getCreditCardUseCase;
        this.listActivityUseCase = listActivityUseCase;
        this.createPurchaseUseCase = createPurchaseUseCase;
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @PostMapping
    public ResponseEntity<CreditCardResponse> create(@AuthenticationPrincipal AuthenticatedUser principal,
                                                     @Valid @RequestBody CreditCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createCreditCardUseCase.execute(principal.id(), request));
    }

    @GetMapping
    public ResponseEntity<List<CreditCardResponse>> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(listCreditCardsUseCase.execute(principal.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCardResponse> getById(@AuthenticationPrincipal AuthenticatedUser principal,
                                                      @PathVariable Long id) {
        return ResponseEntity.ok(getCreditCardUseCase.execute(principal.id(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponse> update(@AuthenticationPrincipal AuthenticatedUser principal,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody CreditCardRequest request) {
        return ResponseEntity.ok(updateCreditCardUseCase.execute(principal.id(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser principal,
                                       @PathVariable Long id) {
        deleteCreditCardUseCase.execute(principal.id(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<List<CreditCardActivityResponse>> listActivity(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(listActivityUseCase.execute(principal.id(), id));
    }

    @PostMapping("/{id}/purchases")
    public ResponseEntity<CreditCardPurchaseResponse> createPurchase(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id,
            @Valid @RequestBody CreditCardPurchaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createPurchaseUseCase.execute(principal.id(), id, request));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<CreditCardPaymentResponse> createPayment(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id,
            @Valid @RequestBody CreditCardPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createPaymentUseCase.execute(principal.id(), id, request));
    }
}
