package com.forguta.libs.saga.core.model;

import com.forguta.commons.util.MDCContext;
import com.forguta.libs.saga.core.config.EnvironmentContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Event<T extends EventPayload<? extends Serializable>> implements Serializable {

    @Builder.Default
    private final String id = UUID.randomUUID().toString();
    @Builder.Default
    private final long timestamp = Instant.now().toEpochMilli();
    @Builder.Default
    private String correlationId = MDCContext.getCorrelationId() != null ? MDCContext.getCorrelationId() : UUID.randomUUID().toString();

    private boolean async;
    private String name;
    private T body;

    private String processedBy;
    private boolean processed;
    private boolean success;
    private String failedMessage;

    public void successedProcessed() {
        this.processedBy = EnvironmentContext.getApplicationName();
        this.processed = true;
        this.success = true;
    }

    public void failedProcessed(Exception exception) {
        this.processedBy = EnvironmentContext.getApplicationName();
        this.processed = true;
        this.success = false;
        this.failedMessage = StringUtils.hasText(exception.getMessage()) ? exception.getClass().getSimpleName().concat(" - ").concat(exception.getMessage()) : exception.getClass().getSimpleName();
    }

    public static <T extends EventPayload<? extends Serializable>> EventBuilder<T> builder() {
        return new CustomEventBuilder<>();
    }

    private static class CustomEventBuilder<T extends EventPayload<? extends Serializable>> extends EventBuilder<T> {
        @Override
        public Event<T> build() {
            super.name(super.body.getClass().getSimpleName());
            return super.build();
        }
    }
}
