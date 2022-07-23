package com.forguta.libs.saga.core.config.rabbit.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@ConfigurationProperties
public class ReQueueProperties extends AbstractConfig {

    private boolean enabled;
    @NestedConfigurationProperty
    private ExchangeProperties exchange;
    @NestedConfigurationProperty
    private QueueProperties queue;
    private String routingKey;
    private boolean autoRequeueEnabled;
    private String cron;
    private int messageCount;

    public boolean validate() {
        boolean valid = true;
        valid = validate("exchange", exchange, valid);
        valid = validate("queue", queue, valid);
        if (StringUtils.hasText(routingKey)) {
            log.error("Invalid RoutingKey : RoutingKey must be provided for requeue configuration");
            valid = false;
        }
        if (autoRequeueEnabled && StringUtils.hasText(cron)) {
            log.error("Invalid Cron : Cron must be provided for auto requeue configuration");
            valid = false;
        }
        if (valid) {
            log.info("Requeue configuration validated successfully : '{}'", this);
        }
        return valid;
    }

    private boolean validate(String key, AbstractConfig abstractConfig, boolean valid) {
        boolean validFlag = valid;
        if (abstractConfig == null) {
            log.error("Invalid {} : {} must be provided for a requeue configuration", key, key);
            validFlag = false;
        } else {
            validFlag = abstractConfig.validate() && validFlag;
        }
        return validFlag;
    }
}
