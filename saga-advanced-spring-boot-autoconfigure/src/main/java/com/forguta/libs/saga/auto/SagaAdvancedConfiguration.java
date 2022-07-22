package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.process.EventProcessorExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@ComponentScan(basePackages = "com.forguta.libs.saga")
@Configuration
public class SagaAdvancedConfiguration {

    private final ApplicationContext applicationContext;

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
    public EventProcessorExecutor eventProcessorExecutor() {
        return new EventProcessorExecutor(applicationContext);
    }

}
