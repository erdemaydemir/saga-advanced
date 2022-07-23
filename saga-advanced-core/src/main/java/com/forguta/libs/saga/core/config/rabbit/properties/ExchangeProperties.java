package com.forguta.libs.saga.core.config.rabbit.properties;

import com.forguta.libs.saga.core.config.AbstractConfig;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@ConfigurationProperties
public class ExchangeProperties extends AbstractConfig {

    private String name;
    private ExchangeTypeEnum type;
    private Boolean durable;
    private Boolean autoDelete;
    private Boolean internal;
    private Boolean delayed;

    @Singular
    private Map<String, Object> arguments;

    public boolean validate() {
        if (StringUtils.hasText(getName())) {
            log.error("Invalid Exchange Configuration : Name must be provided for an exchange");
            return false;
        }
        log.info("Exchange configuration validated successfully for exchange '{}'", getName());
        return true;
    }

    public ExchangeProperties applyDefaultConfig(ExchangeProperties defaultExchangeProperties) {
        log.debug("Applying DefaultExchangeConfig on the current ExchangeConfig :: ExchangeConfig = {{}} , DefaultExchangeConfig = {{}}", this, defaultExchangeProperties);
        setType(getDefaultConfig(getName(), "type", getType(), defaultExchangeProperties.getType(), ExchangeTypeEnum.TOPIC));
        setDurable(getDefaultConfig(getName(), "durable", getDurable(), defaultExchangeProperties.getDurable(), Boolean.FALSE));
        setAutoDelete(getDefaultConfig(getName(), "autoDelete", getAutoDelete(), defaultExchangeProperties.getAutoDelete(), Boolean.FALSE));
        setInternal(getDefaultConfig(getName(), "internal", getInternal(), defaultExchangeProperties.getInternal(), Boolean.FALSE));
        setDelayed(getDefaultConfig(getName(), "delayed", getDelayed(), defaultExchangeProperties.getDelayed(), Boolean.FALSE));
        setArguments(loadArguments(getArguments(), defaultExchangeProperties.getArguments()));
        setDefaultConfigApplied(true);
        log.info("DefaultExchangeConfig applied on the current ExchangeConfig :: ExchangeConfig = {{}} , DefaultExchangeConfig = {{}}", this, defaultExchangeProperties);
        return this;
    }

    public AbstractExchange buildExchange(ExchangeProperties defaultExchangeProperties) {
        if (!isDefaultConfigApplied()) {
            applyDefaultConfig(defaultExchangeProperties);
        }
        AbstractExchange exchange = new CustomExchange(getName(), getType().getValue(), getDurable(), getAutoDelete(), getArguments());
        exchange.setInternal(getInternal());
        exchange.setDelayed(getDelayed());
        return exchange;
    }
}
