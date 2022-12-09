package com.forguta.libs.saga.core.exception;

public class ProcessInternalException extends RuntimeException {

    public ProcessInternalException() {
    }

    public ProcessInternalException(String message) {
        super(message);
    }
}
