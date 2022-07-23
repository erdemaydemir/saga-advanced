package com.forguta.libs.saga.core.config;

import org.springframework.context.ApplicationContext;

public interface SagaMessageBrokerConfigurer {

    public void settingUp(ApplicationContext applicationContext);

    public void initialize();
}
