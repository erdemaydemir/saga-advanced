package com.forguta.libs.saga.core.handler;

import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.constant.Constant;
import com.forguta.libs.saga.core.model.constant.EventActionTypeEnum;
import com.forguta.libs.saga.core.process.EventProcessorExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @RabbitListener(queues = "#{T(com.forguta.libs.saga.core.broker.rabbit.RabbitConfigurer).getSagaQueue()}")
    public <T extends Serializable> void receive(Event<T> event) {
        log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.RECEIVED, event.getId(), event.getCorrelationId(), event.isAsync() ? "sync-mode = {}" : Constant.SYNC, event.getBody());
        applicationEventPublisher.publishEvent(event);
    }

    @EventListener(classes = Event.class, condition = "!#event.async")
    public <T extends Serializable> void handleEvent(Event<T> event) {
        try {
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.HANDLED, event.getId(), event.getCorrelationId(), event.isAsync() ? "sync-mode = {}" : Constant.SYNC, event.getBody());
            event = EventProcessorExecutor.execute(event).get();
            event.successedProcessed();
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.SUCCESSED, event.getId(), event.getCorrelationId(), event.isAsync() ? "sync-mode = {}" : Constant.SYNC, event.getBody());
        } catch (ProcessInternalException | InterruptedException | ExecutionException | InvocationTargetException |
                 IllegalAccessException exception) {
            event.failedProcessed(exception);
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, failure message = {}, body = {}", event.getName(), EventActionTypeEnum.FAILED, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getFailedMessage(), event.getBody());
        } catch (EventProcessorNotFoundException exception) {
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, failure message = {}, body = {}", event.getName(), EventActionTypeEnum.IDLE, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getFailedMessage(), event.getBody());
        }
    }

    @Async
    @EventListener(classes = Event.class, condition = "#event.async")
    public <T extends Serializable> void asyncHandleEvent(Event<T> event) {
        try {
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.HANDLED, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
            EventProcessorExecutor.execute(event);
            event.successedProcessed();
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, body = {}", event.getName(), EventActionTypeEnum.SUCCESSED, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getBody());
        } catch (ProcessInternalException | InvocationTargetException | IllegalAccessException exception) {
            event.failedProcessed(exception);
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, failure message = {}, body = {}", event.getName(), EventActionTypeEnum.FAILED, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getFailedMessage(), event.getBody());
        } catch (EventProcessorNotFoundException exception) {
            log.info("[{}] EVENT [{}] -> id = {}, correlation-id = {}, sync-mode = {}, failure message = {}, body = {}", event.getName(), EventActionTypeEnum.IDLE, event.getId(), event.getCorrelationId(), event.isAsync() ? Constant.ASYNC : Constant.SYNC, event.getFailedMessage(), event.getBody());
        }
    }
}
