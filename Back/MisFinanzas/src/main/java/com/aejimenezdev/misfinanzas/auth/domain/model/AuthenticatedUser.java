package com.aejimenezdev.misfinanzas.auth.domain.model;

public record AuthenticatedUser(Long id, String email, String role) {
}
