package com.aejimenezdev.misfinanzas.auth.domain.ports;

import com.aejimenezdev.misfinanzas.auth.domain.model.AccessTokenClaims;
import com.aejimenezdev.misfinanzas.user.domain.model.User;

public interface TokenProvider {

    String generateAccessToken(User user);

    AccessTokenClaims parseAccessToken(String token);
}
