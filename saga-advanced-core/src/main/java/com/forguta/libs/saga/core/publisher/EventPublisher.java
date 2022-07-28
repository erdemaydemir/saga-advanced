package com.forguta.libs.saga.core.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forguta.libs.saga.core.broker.rabbit.constant.RabbitConstant;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.constant.Constant;
import com.forguta.libs.saga.core.model.constant.EventActionTypeEnum;
import com.forguta.libs.saga.core.util.EventMDCContext;
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

    public <T extends Serializable> void sendAndForget(Event<T> event) {
        if (StringUtils.hasText(EventMDCContext.getCorrelationId())) {
            event.setCorrelationId(EventMDCContext.getCorrelationId());
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
            log.error("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.NOT_SENT, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
        }
        log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.SENT, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
    }

    public <T extends Serializable> void sendAndForget(Event<T> event, String serviceName) {
        if (StringUtils.hasText(EventMDCContext.getCorrelationId())) {
            event.setCorrelationId(EventMDCContext.getCorrelationId());
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
            log.error("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.NOT_SENT, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
        }
        log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.SENT, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
    }
}
