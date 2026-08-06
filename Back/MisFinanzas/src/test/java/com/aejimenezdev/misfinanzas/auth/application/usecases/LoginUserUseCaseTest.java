package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.LoginRequest;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.InvalidCredentialsException;
import com.aejimenezdev.misfinanzas.auth.domain.ports.PasswordEncoder;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private LoginUserUseCase useCase() {
        RefreshTokenIssuer issuer = new RefreshTokenIssuer(refreshTokenRepository, 6_048_000_000L);
        return new LoginUserUseCase(userRepository, passwordEncoder, tokenProvider, issuer, 900_000L);
    }

    private User enabledUser() {
        return new User(1L, "user@example.com", "encoded", "Jane", "Doe", "USER", true,
                Instant.now(), Instant.now());
    }

    @Test
    void logsInWithValidCredentials() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(enabledUser()));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = useCase().login(new LoginRequest("user@example.com", "password123"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void rejectsUnknownEmail() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().login(new LoginRequest("nobody@example.com", "password123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsWrongPassword() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(enabledUser()));
        when(passwordEncoder.matches("wrong-password", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> useCase().login(new LoginRequest("user@example.com", "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsDisabledUser() {
        User disabled = new User(1L, "user@example.com", "encoded", "Jane", "Doe", "USER", false,
                Instant.now(), Instant.now());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(disabled));

        assertThatThrownBy(() -> useCase().login(new LoginRequest("user@example.com", "password123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
