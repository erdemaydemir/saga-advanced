package com.forguta.libs.saga.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Data
@NoArgsConstructor
public class EventPayload<D extends Serializable> implements Serializable {

    private D dto;
}

