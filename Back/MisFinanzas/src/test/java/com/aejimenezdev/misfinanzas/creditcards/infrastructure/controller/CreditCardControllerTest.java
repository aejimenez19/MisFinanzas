package com.aejimenezdev.misfinanzas.creditcards.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.model.AuthenticatedUser;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
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
import com.aejimenezdev.misfinanzas.creditcards.domain.exceptions.CreditCardNotFoundException;
import com.aejimenezdev.misfinanzas.creditcards.domain.model.CreditCardStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CreditCardControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCreditCardUseCase createCreditCardUseCase;

    @MockitoBean
    private UpdateCreditCardUseCase updateCreditCardUseCase;

    @MockitoBean
    private DeleteCreditCardUseCase deleteCreditCardUseCase;

    @MockitoBean
    private ListCreditCardsUseCase listCreditCardsUseCase;

    @MockitoBean
    private GetCreditCardUseCase getCreditCardUseCase;

    @MockitoBean
    private ListCreditCardActivityUseCase listActivityUseCase;

    @MockitoBean
    private CreateCreditCardPurchaseUseCase createPurchaseUseCase;

    @MockitoBean
    private CreateCreditCardPaymentUseCase createPaymentUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUpSecurity() {
        AuthenticatedUser principal = new AuthenticatedUser(USER_ID, "user@example.com", "USER");
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private CreditCardResponse sampleCard() {
        Instant now = Instant.parse("2026-08-01T10:00:00Z");
        return new CreditCardResponse(1L, "Platinum", "Banco Central", "1234",
                new BigDecimal("10000.00"), 10, 15, CreditCardStatus.ACTIVE, new BigDecimal("3420.00"),
                new BigDecimal("34.20"), LocalDate.of(2026, 8, 15), now, now,
                new BigDecimal("3420.00"), new BigDecimal("0.00"), java.util.List.of());
    }

    @Test
    void createReturnsCreatedWithCard() throws Exception {
        when(createCreditCardUseCase.execute(eq(USER_ID), any(CreditCardRequest.class)))
                .thenReturn(sampleCard());

        mockMvc.perform(post("/api/credit-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Platinum\",\"bank\":\"Banco Central\",\"lastFourDigits\":\"1234\","
                                + "\"creditLimit\":10000.00,\"cutoffDay\":10,\"paymentDay\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Platinum"))
                .andExpect(jsonPath("$.lastFourDigits").value("1234"))
                .andExpect(jsonPath("$.pendingBalance").value(3420.00))
                .andExpect(jsonPath("$.limitUsagePercent").value(34.20));
    }

    @Test
    void createRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/credit-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"bank\":\"\",\"creditLimit\":0,"
                                + "\"cutoffDay\":40,\"paymentDay\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsCards() throws Exception {
        when(listCreditCardsUseCase.execute(USER_ID)).thenReturn(List.of(sampleCard()));

        mockMvc.perform(get("/api/credit-cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Platinum"))
                .andExpect(jsonPath("$[0].pendingBalance").value(3420.00));
    }

    @Test
    void getByIdReturnsCard() throws Exception {
        when(getCreditCardUseCase.execute(USER_ID, 1L)).thenReturn(sampleCard());

        mockMvc.perform(get("/api/credit-cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() throws Exception {
        when(getCreditCardUseCase.execute(USER_ID, 999L))
                .thenThrow(new CreditCardNotFoundException("Credit card not found"));

        mockMvc.perform(get("/api/credit-cards/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listActivityReturnsMergedMovements() throws Exception {
        when(listActivityUseCase.execute(USER_ID, 1L)).thenReturn(List.of(
                new CreditCardActivityResponse(2L, "PURCHASE", "Amazon",
                        new BigDecimal("-120.50"), LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 10)),
                new CreditCardActivityResponse(3L, "PAYMENT", "Card Payment",
                        new BigDecimal("500.00"), LocalDate.of(2026, 8, 15), null)
        ));

        mockMvc.perform(get("/api/credit-cards/1/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PURCHASE"))
                .andExpect(jsonPath("$[0].amount").value(-120.50))
                .andExpect(jsonPath("$[1].type").value("PAYMENT"))
                .andExpect(jsonPath("$[1].amount").value(500.00));
    }

    @Test
    void listActivityReturnsNotFoundWhenMissing() throws Exception {
        when(listActivityUseCase.execute(USER_ID, 999L))
                .thenThrow(new CreditCardNotFoundException("Credit card not found"));

        mockMvc.perform(get("/api/credit-cards/999/activity"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturnsUpdatedCard() throws Exception {
        when(updateCreditCardUseCase.execute(eq(USER_ID), eq(1L), any(CreditCardRequest.class)))
                .thenReturn(sampleCard());

        mockMvc.perform(put("/api/credit-cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Platinum\",\"bank\":\"Banco Central\","
                                + "\"creditLimit\":10000.00,\"cutoffDay\":10,\"paymentDay\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Platinum"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/credit-cards/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new CreditCardNotFoundException("Credit card not found"))
                .when(deleteCreditCardUseCase).execute(USER_ID, 999L);

        mockMvc.perform(delete("/api/credit-cards/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPurchaseReturnsCreated() throws Exception {
        Instant now = Instant.parse("2026-08-01T10:00:00Z");
        CreditCardPurchaseResponse response = new CreditCardPurchaseResponse(1L, 1L, "Amazon",
                new BigDecimal("120.50"), LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 10), now);
        when(createPurchaseUseCase.execute(eq(USER_ID), eq(1L), any(CreditCardPurchaseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/credit-cards/1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Amazon\",\"amount\":120.50,\"purchaseDate\":\"2026-08-05\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Amazon"))
                .andExpect(jsonPath("$.billingCycle").value("2026-08-10"));
    }

    @Test
    void createPaymentReturnsCreated() throws Exception {
        Instant now = Instant.parse("2026-08-01T10:00:00Z");
        CreditCardPaymentResponse response = new CreditCardPaymentResponse(1L, 1L,
                new BigDecimal("500.00"), LocalDate.of(2026, 8, 15), now);
        when(createPaymentUseCase.execute(eq(USER_ID), eq(1L), any(CreditCardPaymentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/credit-cards/1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":500.00,\"paymentDate\":\"2026-08-15\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500.00));
    }
}
