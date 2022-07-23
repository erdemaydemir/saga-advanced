package com.forguta.libs.saga.core.config.rabbit;

import com.forguta.libs.saga.core.config.ApplicationProperties;
import com.forguta.libs.saga.core.config.SagaMessageBrokerConfigurer;
import com.forguta.libs.saga.core.config.rabbit.properties.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

import static com.forguta.libs.saga.core.config.rabbit.constant.RabbitConstant.*;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfigurer implements SagaMessageBrokerConfigurer {

    private final RabbitProperties rabbitProperties;

    private RabbitAdmin rabbitAdmin;

    private Exchange sagaExchange;
    private Queue sagaQueue;

    @Override
    public void settingUp(ApplicationContext applicationContext) {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setBeforePublishPostProcessors(applicationContext.getBeansOfType(MessagePostProcessor.class).values().toArray(new MessagePostProcessor[0]));
    }

    @Bean
    @ConditionalOnMissingBean(RabbitAdmin.class)
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        return rabbitAdmin;
    }

    @Override
    public void initialize() {
        Exchange deadLetterExchange = loadSagaDeadLetterExchangeConfig();
        loadSagaExchangeConfigs();
        loadSagaQueueConfigs(deadLetterExchange);
        loadBindingConfigs();
    }

    private Exchange loadSagaDeadLetterExchangeConfig() {
        DeadLetterProperties sagaDeadLetterProperties = createValidDeadLetterConfig(SAGA_DEAD_LETTER_EXCHANGE_DLQ);
        Exchange deadLetterExchange = sagaDeadLetterProperties.getDeadLetterExchange().buildExchange(rabbitProperties.getDefaultExchange());
        rabbitAdmin.declareExchange(deadLetterExchange);
        log.info("Auto configuring dead letter exchange: Key = {} , DeadLetterExchange = {{}}", deadLetterExchange.getName(), deadLetterExchange);
        return deadLetterExchange;
    }

    private void loadSagaExchangeConfigs() {
        log.info("Auto configuring exchange...");
        ExchangeProperties sagaExchangeProperties = createExchangeConfig(SAGA_EXCHANGE);
        sagaExchange = sagaExchangeProperties.buildExchange(rabbitProperties.getDefaultExchange());
        rabbitAdmin.declareExchange(sagaExchange);
        log.info("Auto configuring exchange: Name = {} , Exchange = {{}}", sagaExchange.getName(), sagaExchange);
    }

    private void loadSagaQueueConfigs(Exchange deadLetterExchange) {
        log.info("Auto configuring queue...");
        QueueProperties sagaQueueProperties = createQueueConfig(SAGA_QUEUE, true);
        sagaQueueProperties.setDeadLetterEnabled(true);
        sagaQueue = sagaQueueProperties.buildQueue(rabbitProperties.getDefaultQueue(), rabbitProperties.getDefaultDeadLetter());
        rabbitAdmin.declareQueue(sagaQueue);
        log.info("Auto configuring queue: Name = {} , Queue = {{}}", sagaQueue.getName(), sagaQueue);
        if (sagaQueueProperties.getDeadLetterEnabled()) {
            Queue sagaDeadLetterQueue = sagaQueueProperties.buildDeadLetterQueue(rabbitProperties.getDefaultQueue(), rabbitProperties.getDefaultDeadLetter());
            rabbitAdmin.declareQueue(sagaDeadLetterQueue);
            log.info("Auto configuring dead letter queue: Key = {} , DeadLetterQueue = {{}}", sagaDeadLetterQueue.getName(), sagaDeadLetterQueue);
            Binding deadLetterBinding = BindingBuilder.bind(sagaDeadLetterQueue).to(deadLetterExchange).with(sagaDeadLetterQueue.getName()).noargs();
            rabbitAdmin.declareBinding(deadLetterBinding);
            log.info("Auto configuring dead letter binding: Key = {{}:{}} , DeadLetterBinding = {{}}", deadLetterExchange.getName(), sagaDeadLetterQueue.getName(), deadLetterBinding);
        }
    }

    private void loadBindingConfigs() {
        log.info("Auto configuring binding...");
        BindingProperties sagaBindingProperties = createBinding(sagaExchange.getName(), sagaQueue.getName(), SAGA_ROUTING_KEY, true);
        Binding sagaBinding = sagaBindingProperties.bind(sagaExchange, sagaQueue);
        rabbitAdmin.declareBinding(sagaBinding);
        log.info("Auto configuring binding: Routing Key = {} , Binding = {{}}", sagaBinding.getRoutingKey(), sagaBinding);
    }

    private static ExchangeProperties createExchangeConfig(String name) {
        return ExchangeProperties.builder().name(name).type(ExchangeTypeEnum.TOPIC).autoDelete(true).durable(false).build();
    }

    private static QueueProperties createQueueConfig(String name, boolean postfix) {
        if (postfix) {
            name = appendApplicationNamePostfix(name);
        }
        return QueueProperties.builder().name(name).autoDelete(true).durable(false).deadLetterEnabled(true).build();
    }

    private static BindingProperties createBinding(String exchange, String queue, String routingKey, boolean postfix) {
        if (postfix) {
            routingKey = appendApplicationNamePostfix(routingKey);
        }
        return BindingProperties.builder().exchange(exchange).queue(queue).routingKey(routingKey).build();
    }

    private static DeadLetterProperties createValidDeadLetterConfig(String name) {
        return DeadLetterProperties.builder().deadLetterExchange(ExchangeProperties.builder().name(name).build()).build();
    }

    private static ReQueueProperties createReQueueConfig(String exchange, String queue) {
        return ReQueueProperties.builder().exchange(createExchangeConfig(exchange)).queue(createQueueConfig(queue, true)).routingKey("requeue.key").build();
    }

    private static String appendApplicationNamePostfix(String value) {
        return value.concat("_").concat(ApplicationProperties.getApplicationName().toUpperCase(Locale.ENGLISH));
    }
}


