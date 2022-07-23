package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.config.EventProcessorContextConfig;
import com.forguta.libs.saga.core.config.properties.SagaAdvancedproperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@ComponentScan(basePackages = "com.forguta.libs.saga")
@Configuration
@EnableConfigurationProperties
public class SagaAdvancedConfigurer {

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
    public SagaAdvancedproperties sagaAdvancedConfig() {
        return new SagaAdvancedproperties();
    }

    @Bean
    public EventProcessorContextConfig eventProcessorContextConfig() {
        return new EventProcessorContextConfig(applicationContext);
    }
}
