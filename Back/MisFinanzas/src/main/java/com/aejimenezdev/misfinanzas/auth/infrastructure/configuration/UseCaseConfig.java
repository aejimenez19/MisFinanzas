package com.aejimenezdev.misfinanzas.auth.infrastructure.configuration;

import com.aejimenezdev.misfinanzas.auth.application.services.RefreshTokenIssuer;
import com.aejimenezdev.misfinanzas.auth.application.usecases.LoginUserUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.LogoutUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.RefreshTokenUseCase;
import com.aejimenezdev.misfinanzas.auth.application.usecases.RegisterUserUseCase;
import com.aejimenezdev.misfinanzas.auth.domain.ports.PasswordEncoder;
import com.aejimenezdev.misfinanzas.auth.domain.ports.RefreshTokenRepository;
import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.user.domain.ports.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public UseCaseConfig(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         TokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository,
                         JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public RefreshTokenIssuer refreshTokenIssuer() {
        return new RefreshTokenIssuer(refreshTokenRepository, jwtProperties.refreshTokenExpirationMs());
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(RefreshTokenIssuer refreshTokenIssuer) {
        return new RegisterUserUseCase(userRepository, passwordEncoder, tokenProvider,
                refreshTokenIssuer, jwtProperties.accessTokenExpirationMs());
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(RefreshTokenIssuer refreshTokenIssuer) {
        return new LoginUserUseCase(userRepository, passwordEncoder, tokenProvider,
                refreshTokenIssuer, jwtProperties.accessTokenExpirationMs());
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenIssuer refreshTokenIssuer) {
        return new RefreshTokenUseCase(userRepository, tokenProvider, refreshTokenRepository,
                refreshTokenIssuer, jwtProperties.accessTokenExpirationMs());
    }

    @Bean
    public LogoutUseCase logoutUseCase() {
        return new LogoutUseCase(refreshTokenRepository);
    }
}
