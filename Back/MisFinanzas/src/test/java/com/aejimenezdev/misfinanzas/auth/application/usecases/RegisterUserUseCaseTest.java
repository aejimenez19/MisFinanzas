package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.RegisterRequest;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.UserAlreadyExistsException;
import com.aejimenezdev.misfinanzas.auth.domain.ports.PasswordEncoder;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RegisterUserUseCase useCase() {
        RefreshTokenIssuer issuer = new RefreshTokenIssuer(refreshTokenRepository, 6_048_000_000L);
        return new RegisterUserUseCase(userRepository, passwordEncoder, tokenProvider, issuer, 900_000L);
    }

    @Test
    void registersUserAndReturnsTokens() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(1L, user.email(), user.password(), user.firstName(), user.lastName(),
                    user.role(), user.enabled(), user.createdAt(), user.updatedAt());
        });
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("access-token");

        AuthResponse response = useCase()
                .register(new RegisterRequest("user@example.com", "password123", "Jane", "Doe"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresInMs()).isEqualTo(900_000L);
        assertThat(response.user().email()).isEqualTo("user@example.com");
        assertThat(response.user().role()).isEqualTo("USER");

        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase()
                .register(new RegisterRequest("user@example.com", "password123", "Jane", "Doe")))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(refreshTokenRepository, never()).save(any());
    }
}
