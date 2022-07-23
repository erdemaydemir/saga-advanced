package com.forguta.libs.saga.core.config.rabbit.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import com.forguta.libs.saga.core.exception.rabbit.RabbitmqConfigurationException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@ConfigurationProperties
public class QueueProperties extends AbstractConfig {

    private String name;
    private Boolean durable;
    private Boolean autoDelete;
    private Boolean exclusive;
    private Boolean deadLetterEnabled;

    @Singular
    private Map<String, Object> arguments;

    public boolean validate() {
        if (StringUtils.hasText(getName())) {
            log.error("Invalid Queue Configuration : Name must be provided for a queue");
            return false;
        }
        log.info("Queue configuration validated successfully for queue '{}'", getName());
        return true;
    }

    public QueueProperties applyDefaultConfig(QueueProperties defaultQueueProperties) {
        log.debug("Applying DefaultQueueConfig on the current QueueConfig :: QueueConfig = {{}} , DefaultQueueConfig = {{}}", this, defaultQueueProperties);
        setDurable(getDefaultConfig(getName(), "durable", getDurable(), defaultQueueProperties.getDurable(), Boolean.FALSE));
        setAutoDelete(getDefaultConfig(getName(), "autoDelete", getAutoDelete(), defaultQueueProperties.getAutoDelete(), Boolean.FALSE));
        setExclusive(getDefaultConfig(getName(), "exclusive", getExclusive(), defaultQueueProperties.getExclusive(), Boolean.FALSE));
        setDeadLetterEnabled(getDefaultConfig(getName(), "deadLetterEnabled", getDeadLetterEnabled(), defaultQueueProperties.getDeadLetterEnabled(), Boolean.FALSE));
        setArguments(loadArguments(getArguments(), defaultQueueProperties.getArguments()));
        setDefaultConfigApplied(true);
        log.info("DefaultQueueConfig applied on the current ExchangeConfig :: ExchangeConfig = {{}} , DefaultQueueConfig = {{}}", this, defaultQueueProperties);
        return this;
    }

    public Queue buildQueue(QueueProperties defaultQueueProperties, DeadLetterProperties deadLetterProperties) {
        if (!isDefaultConfigApplied()) {
            applyDefaultConfig(defaultQueueProperties);
        }
        Queue queue = new Queue(getName(), getDurable(), getExclusive(), getAutoDelete(), getArguments());
        if (Boolean.TRUE.equals(getDeadLetterEnabled())) {
            if (deadLetterProperties == null || deadLetterProperties.getDeadLetterExchange() == null) {
                throw new RabbitmqConfigurationException(String.format("Invalid configuration %s : DeadLetterConfig/DeadLetterExchange must be provided when deadLetterEnabled=true for queue %s.", getName(), getName()));
            }
            queue.getArguments().put("x-dead-letter-exchange", deadLetterProperties.getDeadLetterExchange().getName());
            queue.getArguments().put("x-dead-letter-routing-key", deadLetterProperties.createDeadLetterQueueName(getName()));
        }
        return queue;
    }

    public Queue buildDeadLetterQueue(QueueProperties defaultQueueProperties, DeadLetterProperties deadLetterProperties) {
        if (!isDefaultConfigApplied()) {
            applyDefaultConfig(defaultQueueProperties);
        }
        return new Queue(deadLetterProperties.createDeadLetterQueueName(getName()), getDurable(), getExclusive(), getAutoDelete(), getArguments());
    }
}
