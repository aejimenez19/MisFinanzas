package com.aejimenezdev.misfinanzas.auth.application.mapper;

import com.aejimenezdev.misfinanzas.auth.application.dto.UserResponse;
import com.aejimenezdev.misfinanzas.user.domain.model.User;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.id(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.role());
    }
}
