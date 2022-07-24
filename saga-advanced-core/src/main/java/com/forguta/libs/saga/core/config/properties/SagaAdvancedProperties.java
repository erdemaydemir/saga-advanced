package com.forguta.libs.saga.core.config.properties;

import com.forguta.libs.saga.core.broker.rabbit.element.RabbitElement;
import com.forguta.libs.saga.core.exception.SagaAdvancedConfigurationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "saga-advanced")
public class SagaAdvancedProperties {

    private boolean enabled = true;

    @PostConstruct
    public void validate() {
        boolean valid = true;
        if (valid) {
            log.info("SagaAdvancedConfig Validation done successfully. SagaAdvancedConfig = {{}}", this.toString());
        } else {
            throw new SagaAdvancedConfigurationException("Invalid SagaAdvancedConfig Configuration");
        }
    }

    public static boolean validate(String key, RabbitElement rabbitElement, boolean valid) {
        log.info("Validating key {} :: value {}...", key, rabbitElement);
        return rabbitElement.validate() && valid;
    }
}
