package com.forguta.libs.saga.core.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.exception.ProcessRequiredEventCastingException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.model.EventPayload;
import com.forguta.libs.saga.core.util.EventMDCContext;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Component
public class EventProcessorExecutor {

    /**
     * It will be filled with processor {@link Method} by constructor.
     */
    private static final Map<String, BeanMethodCombination> processorMap = new HashMap<>();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
            EventMDCContext.put(event.getCorrelationId());
            biConsumer.accept(getCastedEventPayload(event, beanMethodCombination.getKlass()), beanMethodCombination);
            EventMDCContext.clear();
        } catch (Exception exception) {
            exception.printStackTrace();
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
