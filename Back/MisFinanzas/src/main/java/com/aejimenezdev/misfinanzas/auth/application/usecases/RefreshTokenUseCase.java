package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.RefreshTokenRequest;
import com.aejimenezdev.misfinanzas.auth.application.mapper.AuthMapper;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.InvalidTokenException;
import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;

import java.time.Instant;

public class RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final long accessTokenExpirationMs;

    public RefreshTokenUseCase(UserRepository userRepository, TokenProvider tokenProvider,
                               RefreshTokenRepository refreshTokenRepository,
                               RefreshTokenIssuer refreshTokenIssuer, long accessTokenExpirationMs) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = RefreshToken.hashToken(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        if (stored.revoked() || stored.isExpired(Instant.now())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        User user = userRepository.findById(stored.userId())
                .filter(User::enabled)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        refreshTokenRepository.save(stored.revoke(Instant.now()));
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenIssuer.issue(user.id());
        return AuthResponse.of(accessToken, refreshToken, accessTokenExpirationMs, AuthMapper.toUserResponse(user));
    }
}
