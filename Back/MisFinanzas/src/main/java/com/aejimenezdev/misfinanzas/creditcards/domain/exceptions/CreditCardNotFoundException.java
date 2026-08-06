package com.aejimenezdev.misfinanzas.creditcards.domain.exceptions;

public class CreditCardNotFoundException extends RuntimeException {

    public CreditCardNotFoundException(String message) {
        super(message);
    }
}