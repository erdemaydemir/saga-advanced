package com.forguta.libs.saga.core.broker.rabbit.element;

import com.forguta.libs.saga.core.exception.rabbit.RabbitmqConfigurationException;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
@Data
@Slf4j
public class QueueElement implements RabbitElement {

    private String name;
    @Builder.Default
    private Boolean durable = Boolean.FALSE;
    @Builder.Default
    private Boolean autoDelete = Boolean.FALSE;
    @Builder.Default
    private Boolean exclusive = Boolean.FALSE;
    @Builder.Default
    private Boolean deadLetterEnabled = Boolean.TRUE;

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

    public Queue buildQueue(DeadLetterElement deadLetterElement) {
        Queue queue = new Queue(getName(), getDurable(), getExclusive(), getAutoDelete(), getArguments());
        if (Boolean.TRUE.equals(getDeadLetterEnabled())) {
            if (deadLetterElement == null || deadLetterElement.getDeadLetterExchange() == null) {
                throw new RabbitmqConfigurationException(String.format("Invalid configuration %s : DeadLetterConfig/DeadLetterExchange must be provided when deadLetterEnabled=true for queue %s.", getName(), getName()));
            }
            queue.getArguments().put("x-dead-letter-exchange", deadLetterElement.getDeadLetterExchange().getName());
            queue.getArguments().put("x-dead-letter-routing-key", deadLetterElement.createDeadLetterQueueName(getName()));
        }
        return queue;
    }

    public Queue buildDeadLetterQueue(DeadLetterElement deadLetterElement) {
        return new Queue(deadLetterElement.createDeadLetterQueueName(getName()), getDurable(), getExclusive(), getAutoDelete(), getArguments());
    }
}
