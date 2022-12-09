package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.model.EventPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SampleCreateEvent extends EventPayload<SampleDto> {
}
