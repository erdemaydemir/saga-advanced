package com.forguta.libs.saga.core.process;

import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.exception.ProcessRequiredEventCastingException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.util.EventMDCContext;
import com.forguta.libs.saga.core.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventProcessorExecutor {

    /**
     * It will be filled with processor {@link Method} by constructor.
     */
    private static final Map<String, Pair<Object, Method>> processorMap = new HashMap<>();

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
    public static <T extends Event<?>> CompletableFuture<T> execute(T event) throws EventProcessorNotFoundException, ProcessInternalException, InvocationTargetException, IllegalAccessException {
        Pair<Object, Method> beanMethodPair = processorMap.get(event.getName());
        if (beanMethodPair == null) {
            throw new EventProcessorNotFoundException("Event processor not found for " + event.getName());
        }
        try {
            EventMDCContext.put(event.getCorrelationId());
            Method method = beanMethodPair.getSecond();
            method.invoke(beanMethodPair.getFirst(), event);
            EventMDCContext.clear();
        } catch (Exception exception) {
            throw new ProcessRequiredEventCastingException("Process method cannot be cast required event object.");
        }
        return CompletableFuture.completedFuture(event);
    }

    public static void putProcessor(String processorName, Pair<Object, Method> processorPair) {
        EventProcessorExecutor.processorMap.put(processorName, processorPair);
    }
}
