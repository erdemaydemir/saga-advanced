package com.forguta.libs.saga.core.config;

import com.forguta.libs.saga.core.broker.SagaMessageBrokerConfiguration;
import com.forguta.libs.saga.core.broker.rabbit.RabbitConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@ComponentScan(basePackages = "com.forguta.libs.saga")
@Configuration
public class SagaAdvancedConfigurer {

    private final ApplicationContext applicationContext;
    private final RabbitTemplate rabbitTemplate;

    @Bean
    @Qualifier("eventTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Event-Async");
        executor.initialize();
        return executor;
    }

    @Bean
    public EnvironmentContext environmentContext() {
        return new EnvironmentContext(applicationContext);
    }

    @Bean
    @DependsOn("environmentContext")
    public SagaMessageBrokerConfiguration sagaMessageBrokerConfiguration() {
        return new RabbitConfiguration(rabbitTemplate);
    }

    @Bean
    @DependsOn("environmentContext")
    public EventProcessorContextConfig eventProcessorContextConfig() {
        return new EventProcessorContextConfig(applicationContext);
    }
}
