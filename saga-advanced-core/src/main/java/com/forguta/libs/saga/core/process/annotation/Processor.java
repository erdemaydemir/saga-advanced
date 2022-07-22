package com.forguta.libs.saga.core.process.annotation;

import com.forguta.libs.saga.core.model.Event;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Component
public @interface Processor {

    Class<? extends Event<?>> value();

}
