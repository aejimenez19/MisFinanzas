package com.aejimenezdev.misfinanzas.user.domain.ports;

import com.aejimenezdev.misfinanzas.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
}
