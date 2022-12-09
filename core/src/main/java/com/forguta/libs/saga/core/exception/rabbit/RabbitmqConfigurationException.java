package com.forguta.libs.saga.core.exception.rabbit;

public class RabbitmqConfigurationException extends RuntimeException {

    public RabbitmqConfigurationException() {
    }

    public RabbitmqConfigurationException(String message) {
        super(message);
    }
}
