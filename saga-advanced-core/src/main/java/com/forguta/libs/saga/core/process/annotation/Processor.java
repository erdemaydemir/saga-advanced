package com.forguta.libs.saga.core.process.annotation;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Component
public @interface Processor {

    Class<? extends Serializable> value();

}
