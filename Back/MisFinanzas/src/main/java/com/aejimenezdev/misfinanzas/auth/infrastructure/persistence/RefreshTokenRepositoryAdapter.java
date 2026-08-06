package com.aejimenezdev.misfinanzas.auth.infrastructure.persistence;

import com.aejimenezdev.misfinanzas.auth.domain.model.RefreshToken;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return toDomain(jpaRepository.save(toEntity(refreshToken)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(entity.getId(), entity.getTokenHash(), entity.getUserId(),
                entity.getExpiresAt(), entity.isRevoked(), entity.getRevokedAt(), entity.getCreatedAt());
    }

    private RefreshTokenJpaEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(refreshToken.id(), refreshToken.tokenHash(), refreshToken.userId(),
                refreshToken.expiresAt(), refreshToken.revoked(), refreshToken.revokedAt(), refreshToken.createdAt());
    }
}
