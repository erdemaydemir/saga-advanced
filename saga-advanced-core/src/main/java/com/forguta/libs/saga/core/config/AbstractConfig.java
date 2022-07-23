package com.forguta.libs.saga.core.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Slf4j
public abstract class AbstractConfig {

    private boolean defaultConfigApplied;

    protected <T> T getDefaultConfig(String name, String property, T currentValue, T defaultConfigValue, T defaultValue) {
        T value = getDefaultConfig(currentValue, defaultConfigValue);
        if (null == value) {
            log.warn("'{}' : '{}' : No '{}' configuration provided. Applying default value {} : {} ", getClass().getName(), name, property, property, defaultValue);
        }
        return value != null ? value : defaultValue;
    }

    protected <T> T getDefaultConfig(T currentValue, T defaultConfigValue) {
        return currentValue != null ? currentValue : defaultConfigValue;
    }

    protected Map<String, Object> loadArguments(Map<String, Object> currentArguments, Map<String, Object> defaultArguments) {
        Map<String, Object> arguments = new HashMap<>();
        if (defaultArguments != null) {
            arguments.putAll(defaultArguments);
        }
        if (currentArguments != null) {
            arguments.putAll(currentArguments);
        }
        return arguments;
    }

    public abstract boolean validate();
}
