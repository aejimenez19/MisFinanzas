package com.aejimenezdev.misfinanzas.auth.infrastructure.adapter;

import com.aejimenezdev.misfinanzas.auth.domain.exceptions.InvalidTokenException;
import com.aejimenezdev.misfinanzas.auth.domain.model.AccessTokenClaims;
import com.aejimenezdev.misfinanzas.auth.infrastructure.configuration.JwtProperties;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET =
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private final JwtTokenProvider provider = new JwtTokenProvider(
            new JwtProperties(SECRET, 900_000L, 6_048_000_000L));

    @Test
    void generatesAndParsesAccessToken() {
        User user = new User(42L, "user@example.com", "encoded", "Jane", "Doe", "USER", true,
                Instant.now(), Instant.now());

        String token = provider.generateAccessToken(user);
        AccessTokenClaims claims = provider.parseAccessToken(token);

        assertThat(claims.userId()).isEqualTo(42L);
        assertThat(claims.email()).isEqualTo("user@example.com");
        assertThat(claims.role()).isEqualTo("USER");
    }

    @Test
    void rejectsGarbageToken() {
        assertThatThrownBy(() -> provider.parseAccessToken("not-a-jwt"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
