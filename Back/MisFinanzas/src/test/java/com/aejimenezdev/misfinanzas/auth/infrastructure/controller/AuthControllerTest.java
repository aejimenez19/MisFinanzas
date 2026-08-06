package com.aejimenezdev.misfinanzas.auth.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.LoginRequest;
import com.aejimenezdev.misfinanzas.auth.application.dto.RegisterRequest;
import com.aejimenezdev.misfinanzas.auth.application.dto.UserResponse;
import com.aejimenezdev.misfinanzas.auth.application.usecases.LoginUserUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.LogoutUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.RefreshTokenUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.RegisterUserUseCase;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @MockitoBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void registerReturnsCreatedWithTokens() throws Exception {
        UserResponse user = new UserResponse(1L, "user@example.com", "Jane", "Doe", "USER");
        when(registerUserUseCase.register(any(RegisterRequest.class)))
                .thenReturn(AuthResponse.of("access-token", "refresh-token", 900_000L, user));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password123\","
                                + "\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    void registerRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginReturnsTokens() throws Exception {
        UserResponse user = new UserResponse(1L, "user@example.com", "Jane", "Doe", "USER");
        when(loginUserUseCase.login(any(LoginRequest.class)))
                .thenReturn(AuthResponse.of("access-token", "refresh-token", 900_000L, user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void logoutReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"some-refresh\"}"))
                .andExpect(status().isNoContent());
    }
}
