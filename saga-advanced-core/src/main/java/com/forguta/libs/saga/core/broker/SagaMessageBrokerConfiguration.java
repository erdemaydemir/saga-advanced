package com.forguta.libs.saga.core.broker;

import javax.annotation.PostConstruct;

public abstract class SagaMessageBrokerConfiguration {

    @PostConstruct
    public abstract void init();
}
