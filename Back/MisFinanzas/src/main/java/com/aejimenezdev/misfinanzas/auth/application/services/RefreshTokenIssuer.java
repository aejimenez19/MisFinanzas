package com.aejimenezdev.misfinanzas.auth.application.services;

import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

public class RefreshTokenIssuer {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpirationMs;

    public RefreshTokenIssuer(RefreshTokenRepository refreshTokenRepository, long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String issue(Long userId) {
        String rawToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.create(
                rawToken, userId, Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }
}
