package com.aejimenezdev.misfinanzas.category.domain.exceptions;

public class InvalidCategoryException extends RuntimeException {

    public InvalidCategoryException(String message) {
        super(message);
    }
}
