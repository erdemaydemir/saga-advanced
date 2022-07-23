package com.forguta.libs.saga.core.config.rabbit.properties;

public enum ExchangeTypeEnum {
    DIRECT("direct"), TOPIC("topic"), FANOUT("fanout"), HEADERS("headers");

    private String value;

    ExchangeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
