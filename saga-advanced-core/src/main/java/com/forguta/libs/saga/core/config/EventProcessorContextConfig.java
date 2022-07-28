package com.forguta.libs.saga.core.config;

import com.forguta.libs.saga.core.exception.NoSuchProcessorDefinitionException;
import com.forguta.libs.saga.core.process.BeanMethodCombination;
import com.forguta.libs.saga.core.process.EventProcessorExecutor;
import com.forguta.libs.saga.core.process.annotation.Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class EventProcessorContextConfig {

    private final ApplicationContext applicationContext;

    /**
     * Get all methods. Mapped with eventName as key.
     * Searching for {@link Processor} annotation top of Methods in Service, Component beans.
     *
     * @throws NoSuchProcessorDefinitionException
     */
    @PostConstruct
    public void init() {
        try {
            loadProcessorMap();
        } catch (NoSuchProcessorDefinitionException exception) {
            log.info("There is no processor available.");
        }
    }

    private void loadProcessorMap() {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);
        if (beans.size() > 0) {
            beans.forEach((s, bean) -> {
                Class<?> klass = bean.getClass();
                while (klass != Object.class) {
                    for (final Method method : klass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Processor.class)) {
                            Processor annotInstance = method.getAnnotation(Processor.class);
                            EventProcessorExecutor.putProcessor(annotInstance.value().getSimpleName(), BeanMethodCombination.builder()
                                    .bean(bean).method(method).klass(annotInstance.value()).build());
                        }
                    }
                    klass = klass.getSuperclass();
                }
            });
        } else {
            throw new NoSuchProcessorDefinitionException("New instance must be created. It should use to be Processor annotation on top of method..");
        }
    }
}
