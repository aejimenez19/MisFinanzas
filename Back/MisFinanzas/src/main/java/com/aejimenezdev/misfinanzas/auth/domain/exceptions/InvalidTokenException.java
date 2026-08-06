package com.aejimenezdev.misfinanzas.auth.domain.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
