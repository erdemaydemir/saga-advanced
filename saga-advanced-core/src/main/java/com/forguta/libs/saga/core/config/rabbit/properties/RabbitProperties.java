package com.forguta.libs.saga.core.config.rabbit.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import com.forguta.libs.saga.core.exception.rabbit.RabbitmqConfigurationException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@ConfigurationProperties
public class RabbitProperties extends AbstractConfig {

    @NestedConfigurationProperty
    private ExchangeProperties defaultExchange;

    @NestedConfigurationProperty
    private QueueProperties defaultQueue;

    @NestedConfigurationProperty
    private DeadLetterProperties defaultDeadLetter;

    @Override
    public boolean validate() {
        boolean valid = true;
        if (valid) {
            log.info("RabbitConfig Validation done successfully. RabbitConfig = {{}}", this);
        } else {
            throw new RabbitmqConfigurationException("Invalid RabbitConfig Configuration");
        }
        return true;
    }
}
