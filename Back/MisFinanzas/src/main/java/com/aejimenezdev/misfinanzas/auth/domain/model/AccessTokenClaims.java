package com.aejimenezdev.misfinanzas.auth.domain.model;

public record AccessTokenClaims(Long userId, String email, String role) {
}
