package com.forguta.libs.saga.core.util;

import org.slf4j.MDC;

public class EventMDCContext {

    private static final String CORRELATION_ID = "correlation-id";

    public static void put(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
    }

    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }

    public static void clear() {
        MDC.clear();
    }
}
