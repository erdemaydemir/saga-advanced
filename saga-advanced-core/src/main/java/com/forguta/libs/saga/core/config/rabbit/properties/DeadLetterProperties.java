package com.forguta.libs.saga.core.config.rabbit.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.forguta.libs.saga.core.config.rabbit.constant.RabbitConstant.DEFAULT_DEAD_LETTER_QUEUE_POSTFIX;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@ConfigurationProperties
public class DeadLetterProperties extends AbstractConfig {

    @NestedConfigurationProperty
    private ExchangeProperties deadLetterExchange;

    @Builder.Default
    private String queuePostfix = DEFAULT_DEAD_LETTER_QUEUE_POSTFIX;

    public String createDeadLetterQueueName(String queueName) {
        return queueName + getDefaultConfig("DeadLetterConfig", "queuePostfix", queuePostfix, null, DEFAULT_DEAD_LETTER_QUEUE_POSTFIX);
    }

    @Override
    public boolean validate() {
        log.info("Validating DeadLetterConfig...");
        if (deadLetterExchange != null && deadLetterExchange.validate()) {
            log.info("DeadLetterConfig configuration validated successfully for deadLetterExchange '{}'", deadLetterExchange);
            return true;
        }
        log.error("Invalid DeadLetterConfig Configuration : Valid DeadLetterExchange must be provided for DeadLetterConfig");
        return false;
    }
}
