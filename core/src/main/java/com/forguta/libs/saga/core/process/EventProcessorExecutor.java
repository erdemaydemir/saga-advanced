package com.forguta.libs.saga.core.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forguta.commons.constant.LogSummonerEnum;
import com.forguta.commons.constant.LogSummonerPhaseEnum;
import com.forguta.commons.util.MDCContext;
import com.forguta.commons.util.MyObjectMapper;
import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.exception.ProcessRequiredEventCastingException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.EventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class EventProcessorExecutor {

    /**
     * It will be filled with processor {@link Method} by constructor.
     */
    private static final Map<String, BeanMethodCombination> processorMap = new HashMap<>();
    private static MyObjectMapper OBJECT_MAPPER;

    public EventProcessorExecutor(MyObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }

    /**
     * This method allows the event to be processed by selecting
     * the process that the event belongs to using a strategy design pattern.
     *
     * @param event will be process event
     * @return processed event.
     * @throws EventProcessorNotFoundException
     * @throws ProcessInternalException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T extends EventPayload<? extends Serializable>> CompletableFuture<Event<T>> execute(Event<T> event) throws EventProcessorNotFoundException, ProcessInternalException, InvocationTargetException, IllegalAccessException {
        BeanMethodCombination beanMethodCombination = processorMap.get(event.getName());
        if (beanMethodCombination == null) {
            throw new EventProcessorNotFoundException("Event processor not found for " + event.getName());
        }
        try {
            MDCContext.putCorrelationId(event.getCorrelationId());
            log.info("TYPE={}, PHASE={}, EVENT_NAME={}, EVENT_ID={}", LogSummonerEnum.EVENT, LogSummonerPhaseEnum.PRE_HANDLE, event.getName(), event.getId());
            biConsumer.accept(getCastedEventPayload(event, beanMethodCombination.getKlass()), beanMethodCombination);
            log.info("TYPE={}, PHASE={}, EVENT_NAME={}, EVENT_ID={}", LogSummonerEnum.EVENT, LogSummonerPhaseEnum.POST_HANDLE, event.getName(), event.getId());
            MDCContext.clear();
        } catch (Exception exception) {
            throw new ProcessRequiredEventCastingException("Process method cannot be cast required event object.");
        }
        return CompletableFuture.completedFuture(event);
    }

    public static void putProcessor(String processorName, BeanMethodCombination beanMethodCombination) {
        EventProcessorExecutor.processorMap.put(processorName, beanMethodCombination);
    }

    private static <T extends EventPayload<? extends Serializable>> EventPayload<?> getCastedEventPayload(Event<T> event, Class<?> klass) throws JsonProcessingException {
        String eventBodyJsonStr = OBJECT_MAPPER.writeValueAsString(event.getBody());
        return (EventPayload<?>) OBJECT_MAPPER.readValue(eventBodyJsonStr, klass);
    }

    private final static BiConsumer<EventPayload<?>, BeanMethodCombination> biConsumer = (eventPayload, beanMethodCombination) -> {
        try {
            beanMethodCombination.getMethod().invoke(beanMethodCombination.getBean(), eventPayload);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };
}
