package com.forguta.libs.saga.core.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BeanMethodCombination {

    private Object bean;
    private Method method;
    private Class<?> klass;
}
