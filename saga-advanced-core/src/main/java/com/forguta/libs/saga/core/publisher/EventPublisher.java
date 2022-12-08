package com.forguta.libs.saga.core.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forguta.commons.util.MDCContext;
import com.forguta.libs.saga.core.broker.rabbit.constant.RabbitConstant;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisher {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RabbitTemplate rabbitTemplate;

    public <T extends EventPayload<? extends Serializable>> void sendAndForget(Event<T> event) {
        String correlationId = MDCContext.getCorrelationId();
        if (StringUtils.hasText(correlationId)) {
            event.setCorrelationId(correlationId);
        }
        try {
            String orderJson = OBJECT_MAPPER.writeValueAsString(event);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setHeader(RabbitConstant.SAGA_HEADER_ALL_NAME, true)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            rabbitTemplate.convertAndSend(RabbitConstant.SAGA_EXCHANGE, RabbitConstant.SAGA_ROUTING_KEY, message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    public <T extends EventPayload<? extends Serializable>> void sendAndForget(Event<T> event, String serviceName) {
        String correlationId = MDCContext.getCorrelationId();
        if (StringUtils.hasText(correlationId)) {
            event.setCorrelationId(correlationId);
        }
        try {
            String orderJson = OBJECT_MAPPER.writeValueAsString(event);
            Message message = MessageBuilder
                    .withBody(orderJson.getBytes())
                    .setHeader(RabbitConstant.SAGA_HEADER_NAME, serviceName)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            rabbitTemplate.convertAndSend(RabbitConstant.SAGA_EXCHANGE, RabbitConstant.SAGA_ROUTING_KEY, message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
