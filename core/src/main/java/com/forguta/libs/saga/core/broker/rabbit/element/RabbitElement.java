package com.forguta.libs.saga.core.broker.rabbit.element;

import java.util.HashMap;
import java.util.Map;

public interface RabbitElement {

    boolean validate();

    default <T> T getDefaultConfig(T currentValue, T defaultConfigValue) {
        return currentValue != null ? currentValue : defaultConfigValue;
    }

    default <T> T getDefaultConfig(String name, String property, T currentValue, T defaultConfigValue, T defaultValue) {
        T value = getDefaultConfig(currentValue, defaultConfigValue);
        return value != null ? value : defaultValue;
    }

    default Map<String, Object> loadArguments(Map<String, Object> currentArguments, Map<String, Object> defaultArguments) {
        Map<String, Object> arguments = new HashMap<>();
        if (defaultArguments != null) {
            arguments.putAll(defaultArguments);
        }
        if (currentArguments != null) {
            arguments.putAll(currentArguments);
        }
        return arguments;
    }
}
