package com.forguta.libs.saga.core.broker.rabbit.element;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
@Data
@Slf4j
public class ExchangeElement implements RabbitElement {

    private String name;
    @Builder.Default
    private ExchangeTypeEnum type = ExchangeTypeEnum.TOPIC;
    @Builder.Default
    private Boolean durable = Boolean.FALSE;
    @Builder.Default
    private Boolean autoDelete = Boolean.TRUE;
    @Builder.Default
    private Boolean internal = Boolean.FALSE;
    @Builder.Default
    private Boolean delayed = Boolean.FALSE;

    @Singular
    private Map<String, Object> arguments;

    public boolean validate() {
        if (StringUtils.hasText(getName())) {
            log.error("Invalid Exchange Configuration : Name must be provided for an exchange");
            return false;
        }
        log.info("Exchange configuration validated successfully for exchange '{}'", getName());
        return true;
    }

    public AbstractExchange buildExchange() {
        AbstractExchange exchange = new CustomExchange(getName(), getType().getValue(), getDurable(), getAutoDelete(), getArguments());
        exchange.setInternal(getInternal());
        exchange.setDelayed(getDelayed());
        return exchange;
    }
}
