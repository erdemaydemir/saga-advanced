package com.forguta.libs.saga.core.broker.rabbit.constant;

public class RabbitConstant {
    public static final String SAGA_EXCHANGE = "SAGA-EXCHANGE";
    public static final String SAGA_QUEUE = "SAGA-QUEUE";
    public static final String SAGA_HEADER_NAME = "SAGA-SERVICE";
    public static final String SAGA_HEADER_ALL_NAME = "SAGA-SERVICE_ALL";
    public static final String SAGA_ROUTING_KEY = "SAGA-ROUTING-KEY";
    public static final String X_MATCH_HEADER_NAME = "x-match";
    public static final String SAGA_DEAD_LETTER_EXCHANGE_DLQ = "SAGA-DEAD-LETTER-EXCHANGE.DLQ";
    public static final String DEFAULT_DEAD_LETTER_QUEUE_POSTFIX = ".DLQ";
}
