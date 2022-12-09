package com.forguta.libs.saga.core.broker.rabbit;

import com.forguta.libs.saga.core.broker.rabbit.element.*;
import com.forguta.libs.saga.core.config.EnvironmentContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Locale;

import static com.forguta.libs.saga.core.broker.rabbit.constant.RabbitConstant.*;

@RequiredArgsConstructor
@Slf4j
public class RabbitConfigurer {

    private final RabbitAdmin rabbitAdmin;

    private Exchange sagaExchange;
    private Queue sagaQueue;

    public void initialize() {
        Exchange deadLetterExchange = loadSagaDeadLetterExchangeConfig();
        loadSagaExchangeConfigs();
        loadSagaQueueConfigs(deadLetterExchange);
        loadBindingConfigs();
    }

    private Exchange loadSagaDeadLetterExchangeConfig() {
        DeadLetterElement sagaDeadLetterElement = createValidDeadLetterConfig(SAGA_DEAD_LETTER_EXCHANGE_DLQ);
        Exchange deadLetterExchange = sagaDeadLetterElement.getDeadLetterExchange().buildExchange();
        rabbitAdmin.declareExchange(deadLetterExchange);
        log.info("Auto configuring dead letter exchange: Key = {} , DeadLetterExchange = {{}}", deadLetterExchange.getName(), deadLetterExchange);
        return deadLetterExchange;
    }

    private void loadSagaExchangeConfigs() {
        log.info("Auto configuring exchange...");
        ExchangeElement sagaExchangeElement = createExchangeConfig(SAGA_EXCHANGE);
        sagaExchange = sagaExchangeElement.buildExchange();
        rabbitAdmin.declareExchange(sagaExchange);
        log.info("Auto configuring exchange: Name = {} , Exchange = {{}}", sagaExchange.getName(), sagaExchange);
    }

    private void loadSagaQueueConfigs(Exchange deadLetterExchange) {
        log.info("Auto configuring queue...");
        QueueElement sagaQueueElement = createQueueConfig(SAGA_QUEUE, true);
        sagaQueueElement.setDeadLetterEnabled(true);
        DeadLetterElement sagaDeadLetterElement = createValidDeadLetterConfig(SAGA_DEAD_LETTER_EXCHANGE_DLQ);
        sagaQueue = sagaQueueElement.buildQueue(sagaDeadLetterElement);
        rabbitAdmin.declareQueue(sagaQueue);
        log.info("Auto configuring queue: Name = {} , Queue = {{}}", sagaQueue.getName(), sagaQueue);
        if (sagaQueueElement.getDeadLetterEnabled()) {
            Queue sagaDeadLetterQueue = sagaQueueElement.buildDeadLetterQueue(sagaDeadLetterElement);
            rabbitAdmin.declareQueue(sagaDeadLetterQueue);
            log.info("Auto configuring dead letter queue: Key = {} , DeadLetterQueue = {{}}", sagaDeadLetterQueue.getName(), sagaDeadLetterQueue);
            Binding deadLetterBinding = BindingBuilder.bind(sagaDeadLetterQueue).to(deadLetterExchange).with(sagaDeadLetterQueue.getName()).noargs();
            rabbitAdmin.declareBinding(deadLetterBinding);
            log.info("Auto configuring dead letter binding: Key = {{}:{}} , DeadLetterBinding = {{}}", deadLetterExchange.getName(), sagaDeadLetterQueue.getName(), deadLetterBinding);
        }
    }

    private void loadBindingConfigs() {
        log.info("Auto configuring binding...");
        BindingElement sagaBindingElement = createBinding(sagaExchange.getName(), sagaQueue.getName());
        Binding sagaBinding = sagaBindingElement.bind(sagaExchange, sagaQueue);
        rabbitAdmin.declareBinding(sagaBinding);
        log.info("Auto configuring binding: Routing Key = {} , Binding = {{}}", sagaBinding.getRoutingKey(), sagaBinding);
    }

    private static ExchangeElement createExchangeConfig(String name) {
        return ExchangeElement.builder().name(name).type(ExchangeTypeEnum.HEADERS).build();
    }

    private static QueueElement createQueueConfig(String name, boolean postfix) {
        if (postfix) {
            name = appendApplicationNamePostfix(name);
        }
        return QueueElement.builder().name(name).build();
    }

    private static BindingElement createBinding(String exchange, String queue, String routingKey, boolean postfix) {
        if (postfix) {
            routingKey = appendApplicationNamePostfix(routingKey);
        }
        return BindingElement.builder().exchange(exchange).queue(queue).routingKey(routingKey).build();
    }

    private static BindingElement createBinding(String exchange, String queue) {
        return BindingElement.builder().exchange(exchange).queue(queue)
                .routingKey(SAGA_ROUTING_KEY)
                .argument(SAGA_HEADER_NAME, EnvironmentContext.getApplicationName())
                .argument(SAGA_HEADER_ALL_NAME, true)
                .argument(X_MATCH_HEADER_NAME, "any")
                .build();
    }

    private static DeadLetterElement createValidDeadLetterConfig(String name) {
        return DeadLetterElement.builder().deadLetterExchange(ExchangeElement.builder().name(name).build()).build();
    }

    private static String appendApplicationNamePostfix(String value) {
        return value.concat("_").concat(EnvironmentContext.getApplicationName().toUpperCase(Locale.ENGLISH));
    }

    public static String getSagaQueue() {
        return SAGA_QUEUE.concat("_").concat(EnvironmentContext.getApplicationName().toUpperCase(Locale.ENGLISH));
    }
}


