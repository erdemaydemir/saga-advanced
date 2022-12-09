package com.forguta.libs.saga.core.exception;

public class NoSuchProcessorDefinitionException extends RuntimeException {

    public NoSuchProcessorDefinitionException() {
    }

    public NoSuchProcessorDefinitionException(String message) {
        super(message);
    }
}
