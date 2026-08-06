package com.aejimenezdev.misfinanzas.expense.domain.exceptions;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}