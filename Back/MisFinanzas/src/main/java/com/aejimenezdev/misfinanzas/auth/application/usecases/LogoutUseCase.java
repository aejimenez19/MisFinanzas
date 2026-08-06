package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.RefreshTokenRequest;
import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;

import java.time.Instant;

public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void logout(RefreshTokenRequest request) {
        String tokenHash = RefreshToken.hashToken(request.refreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(token -> !token.revoked())
                .ifPresent(token -> refreshTokenRepository.save(token.revoke(Instant.now())));
    }
}
