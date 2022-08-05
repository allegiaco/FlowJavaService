package com.github.allegiaco.flowjavaservice.exceptions;

public class TransactionException extends Exception {

    public TransactionException (String message, Class<?> clazz) {
        super(String.format(message, clazz.getSimpleName()));
    }

    public TransactionException() {

    }
}
