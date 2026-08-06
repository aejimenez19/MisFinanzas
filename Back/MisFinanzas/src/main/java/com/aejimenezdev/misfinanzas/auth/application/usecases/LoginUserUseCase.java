package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.LoginRequest;
import com.aejimenezdev.misfinanzas.auth.application.mapper.AuthMapper;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.InvalidCredentialsException;
import com.aejimenezdev.misfinanzas.auth.domain.ports.PasswordEncoder;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;

public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final long accessTokenExpirationMs;

    public LoginUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                            TokenProvider tokenProvider, RefreshTokenIssuer refreshTokenIssuer,
                            long accessTokenExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(User::enabled)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenIssuer.issue(user.id());
        return AuthResponse.of(accessToken, refreshToken, accessTokenExpirationMs, AuthMapper.toUserResponse(user));
    }
}
