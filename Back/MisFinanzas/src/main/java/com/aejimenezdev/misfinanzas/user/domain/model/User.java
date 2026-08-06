package com.aejimenezdev.misfinanzas.user.domain.model;

import java.time.Instant;

public record User(
        Long id,
        String email,
        String password,
        String firstName,
        String lastName,
        String role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt) {

    public static final String DEFAULT_ROLE = "USER";

    public static User create(String email, String encodedPassword, String firstName, String lastName) {
        Instant now = Instant.now();
        return new User(null, email, encodedPassword, firstName, lastName, DEFAULT_ROLE, true, now, now);
    }
}
