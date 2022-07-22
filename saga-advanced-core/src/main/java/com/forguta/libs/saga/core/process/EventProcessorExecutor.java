package com.forguta.libs.saga.core.process;

import com.forguta.libs.saga.core.exception.EventProcessorNotFoundException;
import com.forguta.libs.saga.core.exception.NoSuchProcessorDefinitionException;
import com.forguta.libs.saga.core.exception.ProcessInternalException;
import com.forguta.libs.saga.core.exception.ProcessRequiredEventCastingException;
import com.forguta.libs.saga.core.model.Event;
import com.forguta.libs.saga.core.process.annotation.Processor;
import com.forguta.libs.saga.core.util.EventMDCContext;
import com.forguta.libs.saga.core.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class EventProcessorExecutor {

    /**
     * It will be filled with processor {@link Method} by constructor.
     */
    private static Map<String, Pair<Object, Method>> processorMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    /**
     * Get all methods. Mapped with eventName as key.
     * Searching for {@link Processor} annotation top of Methods in Service, Component beans.
     *
     * @throws NoSuchProcessorDefinitionException
     */
    @PostConstruct
    public void init() throws NoSuchProcessorDefinitionException {
        loadProcessorMap();
    }

    private void loadProcessorMap() {
        final Map<String, Object> beansWithComponentAnnotation = applicationContext.getBeansWithAnnotation(Component.class);
        final Map<String, Object> beansWithServiceAnnotation = applicationContext.getBeansWithAnnotation(Service.class);
        final Map<String, Object> beans = Stream.concat(beansWithComponentAnnotation.entrySet().stream(), beansWithServiceAnnotation.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (beans.size() > 0) {
            beans.forEach((s, o) -> {
                Class<?> klass = o.getClass();
                while (klass != Object.class) {
                    for (final Method method : klass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Processor.class)) {
                            Processor annotInstance = method.getAnnotation(Processor.class);
                            processorMap.put(annotInstance.value().getSimpleName(), Pair.of(o, method));
                        }
                    }
                    klass = klass.getSuperclass();
                }
            });
        } else {
            throw new NoSuchProcessorDefinitionException("New instance must be created. It should use to be Processor annotation on top of method..");
        }
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
}
