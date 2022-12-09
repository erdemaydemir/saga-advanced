package com.forguta.libs.saga.core.handler;

import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.EventPayload;
import com.forguta.libs.saga.core.process.EventProcessorExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@DependsOn("sagaMessageBrokerConfiguration")
@Component
public class EventHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @RabbitListener(queues = "#{T(com.forguta.libs.saga.core.broker.rabbit.RabbitConfigurer).getSagaQueue()}")
    public <T extends EventPayload<? extends Serializable>> void receive(Event<T> event) {
        applicationEventPublisher.publishEvent(event);
    }

    @EventListener(classes = Event.class, condition = "!#event.async")
    public <T extends EventPayload<? extends Serializable>> void handleEvent(Event<T> event) {
        try {
            event = EventProcessorExecutor.execute(event).get();
            event.successedProcessed();
        } catch (ProcessInternalException | InterruptedException | ExecutionException | InvocationTargetException |
                 IllegalAccessException exception) {
            event.failedProcessed(exception);
        } catch (EventProcessorNotFoundException exception) {
            doNoting();
        }
    }

    @Async
    @EventListener(classes = Event.class, condition = "#event.async")
    public <T extends EventPayload<? extends Serializable>> void asyncHandleEvent(Event<T> event) {
        try {
            EventProcessorExecutor.execute(event);
            event.successedProcessed();
        } catch (ProcessInternalException | InvocationTargetException | IllegalAccessException exception) {
            event.failedProcessed(exception);
        } catch (EventProcessorNotFoundException exception) {
            doNoting();
        }
    }

    private void doNoting() {
    }
}
