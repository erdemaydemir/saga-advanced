package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.config.SagaMessageBrokerConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
@Import(SagaAdvancedConfigurer.class)
@Slf4j
public class SagaAdvancedAutoConfiguration implements ApplicationContextAware {

    private final SagaMessageBrokerConfigurer iMessageBroker;

    @PostConstruct
    public void init() {
        iMessageBroker.initialize();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        iMessageBroker.settingUp(applicationContext);
    }
}
