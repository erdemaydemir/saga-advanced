package com.forguta.libs.saga.core.broker.rabbit;

import com.forguta.libs.saga.core.broker.SagaMessageBrokerConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RequiredArgsConstructor
@Slf4j
public class RabbitConfiguration extends SagaMessageBrokerConfiguration {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void init() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        RabbitConfigurer rabbitConfigurer = new RabbitConfigurer(rabbitAdmin);
        rabbitConfigurer.initialize();
    }
}


