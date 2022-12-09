package com.forguta.libs.saga.core.exception;

public class ProcessRequiredEventCastingException extends RuntimeException {

    public ProcessRequiredEventCastingException() {
    }

    public ProcessRequiredEventCastingException(String message) {
        super(message);
    }
}
