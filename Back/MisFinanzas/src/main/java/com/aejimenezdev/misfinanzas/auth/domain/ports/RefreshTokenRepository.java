package com.aejimenezdev.misfinanzas.auth.domain.ports;

import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
