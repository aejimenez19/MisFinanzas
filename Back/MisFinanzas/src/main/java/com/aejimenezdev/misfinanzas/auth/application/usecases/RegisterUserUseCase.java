package com.aejimenezdev.misfinanzas.auth.application.usecases;

import com.aejimenezdev.misfinanzas.auth.application.dto.AuthResponse;
import com.aejimenezdev.misfinanzas.auth.application.dto.RegisterRequest;
import com.aejimenezdev.misfinanzas.auth.application.mapper.AuthMapper;
import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.domain.exceptions.UserAlreadyExistsException;
import com.aejimenezdev.misfinanzas.auth.domain.ports.PasswordEncoder;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.model.User;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final long accessTokenExpirationMs;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               TokenProvider tokenProvider, RefreshTokenIssuer refreshTokenIssuer,
                               long accessTokenExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.email());
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.email(), encodedPassword, request.firstName(), request.lastName());
        User saved = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = refreshTokenIssuer.issue(saved.id());
        return AuthResponse.of(accessToken, refreshToken, accessTokenExpirationMs, AuthMapper.toUserResponse(saved));
    }
}
