package com.forguta.libs.saga.core.model;

import com.forguta.libs.saga.core.config.ApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Event<T> implements Serializable {

    private final String id = UUID.randomUUID().toString();
    private final String name = this.getClass().getSimpleName();
    private final long timestamp = Instant.now().toEpochMilli();

    @Builder.Default
    private String correlationId = UUID.randomUUID().toString();

    private boolean async;
    private T body;

    private String processedBy;
    private boolean processed;
    private boolean success;
    private String failedMessage;

    public void successedProcessed() {
        this.processedBy = ApplicationProperties.getApplicationName();
        this.processed = true;
        this.success = true;
    }

    public void failedProcessed(Exception exception) {
        this.processedBy = ApplicationProperties.getApplicationName();
        this.processed = true;
        this.success = false;
        this.failedMessage = StringUtils.hasText(exception.getMessage()) ? exception.getClass().getSimpleName()
                .concat(" - ").concat(exception.getMessage()) : exception.getClass().getSimpleName();
    }
}
