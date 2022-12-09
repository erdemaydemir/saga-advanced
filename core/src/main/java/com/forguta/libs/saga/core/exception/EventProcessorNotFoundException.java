package com.forguta.libs.saga.core.exception;

public class EventProcessorNotFoundException extends RuntimeException {

    public EventProcessorNotFoundException() {
    }

    public EventProcessorNotFoundException(String message) {
        super(message);
    }
}
