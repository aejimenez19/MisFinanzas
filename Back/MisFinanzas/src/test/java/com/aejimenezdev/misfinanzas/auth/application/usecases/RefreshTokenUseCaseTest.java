package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.RefreshTokenRequest;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.InvalidTokenException;
import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;
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
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenUseCase useCase() {
        RefreshTokenIssuer issuer = new RefreshTokenIssuer(refreshTokenRepository, 6_048_000_000L);
        return new RefreshTokenUseCase(userRepository, tokenProvider, refreshTokenRepository, issuer, 900_000L);
    }

    private RefreshToken validToken() {
        return RefreshToken.create("raw-token", 1L, Instant.now().plusSeconds(3600));
    }

    private User enabledUser() {
        return new User(1L, "user@example.com", "encoded", "Jane", "Doe", "USER", true,
                Instant.now(), Instant.now());
    }

    @Test
    void rotatesTokenOnRefresh() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(validToken()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(enabledUser()));
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("new-access");

        AuthResponse response = useCase().refresh(new RefreshTokenRequest("raw-token"));

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isNotBlank();
        verify(refreshTokenRepository).save(argThat(token -> token.revoked() && token.revokedAt() != null));
        verify(refreshTokenRepository).save(argThat(token -> !token.revoked()));
    }

    @Test
    void rejectsUnknownToken() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().refresh(new RefreshTokenRequest("raw-token")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsRevokedToken() {
        RefreshToken revoked = validToken().revoke(Instant.now());
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(revoked));

        assertThatThrownBy(() -> useCase().refresh(new RefreshTokenRequest("raw-token")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsExpiredToken() {
        RefreshToken expired = RefreshToken.create("raw-token", 1L, Instant.now().minusSeconds(60));
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> useCase().refresh(new RefreshTokenRequest("raw-token")))
                .isInstanceOf(InvalidTokenException.class);
    }
}
