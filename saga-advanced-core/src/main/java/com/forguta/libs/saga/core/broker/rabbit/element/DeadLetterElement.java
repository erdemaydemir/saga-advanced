package com.forguta.libs.saga.core.broker.rabbit.element;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.forguta.libs.saga.core.broker.rabbit.constant.RabbitConstant.DEFAULT_DEAD_LETTER_QUEUE_POSTFIX;

@Builder
@Data
@Slf4j
public class DeadLetterElement implements RabbitElement {

    private ExchangeElement deadLetterExchange;

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
