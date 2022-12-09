package com.forguta.libs.saga.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static com.forguta.commons.constant.ApplicationConstant.APPLICATION_NAME_KEY;

@Slf4j
public class EnvironmentContext {

    private static Environment environment;

    public EnvironmentContext(ApplicationContext applicationContext) {
        setEnvironment(applicationContext.getEnvironment());
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getApplicationName() {
        return getProperty(APPLICATION_NAME_KEY);
    }

    public static void setEnvironment(Environment environment) {
        EnvironmentContext.environment = environment;
    }
}
