package com.forguta.libs.saga.core.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

import static com.forguta.commons.constant.ApplicationConstant.APPLICATION_NAME_KEY;

@Slf4j
@AllArgsConstructor
@Configuration
public class ApplicationProperties {

    private final ApplicationContext applicationContext;
    private static Environment environment;

    @PostConstruct
    public void init() {
        setEnvironment(applicationContext.getEnvironment());
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getApplicationName() {
        return getProperty(APPLICATION_NAME_KEY);
    }

    public static void setEnvironment(Environment environment) {
        ApplicationProperties.environment = environment;
    }
}
