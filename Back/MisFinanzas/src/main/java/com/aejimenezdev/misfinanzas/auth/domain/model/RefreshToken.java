package com.aejimenezdev.misfinanzas.auth.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

public record RefreshToken(
        Long id,
        String tokenHash,
        Long userId,
        Instant expiresAt,
        boolean revoked,
        Instant revokedAt,
        Instant createdAt) {

    public static RefreshToken create(String rawToken, Long userId, Instant expiresAt) {
        return new RefreshToken(null, hashToken(rawToken), userId, expiresAt, false, null, Instant.now());
    }

    public RefreshToken revoke(Instant when) {
        return new RefreshToken(id, tokenHash, userId, expiresAt, true, when, createdAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
