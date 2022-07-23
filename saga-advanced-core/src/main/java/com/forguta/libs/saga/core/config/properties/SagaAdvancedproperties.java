package com.forguta.libs.saga.core.config.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import com.forguta.libs.saga.core.config.rabbit.properties.RabbitProperties;
import com.forguta.libs.saga.core.exception.SagaAdvancedConfigurationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.annotation.PostConstruct;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
@ConfigurationProperties(prefix = "saga.auto-config")
public class SagaAdvancedproperties {

    @Builder.Default
    private boolean enabled = true;

    @NestedConfigurationProperty
    private RabbitProperties rabbit;

    @PostConstruct
    public void validate() {
        boolean valid = true;

        if (rabbit != null) {
            log.info("Validating Rabbit Config...");
            valid = validate("RabbitConfig", rabbit, valid);
        }

        if (valid) {
            log.info("SagaAdvancedConfig Validation done successfully. SagaAdvancedConfig = {{}}", this.toString());
        } else {
            throw new SagaAdvancedConfigurationException("Invalid SagaAdvancedConfig Configuration");
        }
    }

    public static boolean validate(String key, AbstractConfig abstractConfig, boolean valid) {
        log.info("Validating key {} :: value {}...", key, abstractConfig);
        return abstractConfig.validate() && valid;
    }
}
