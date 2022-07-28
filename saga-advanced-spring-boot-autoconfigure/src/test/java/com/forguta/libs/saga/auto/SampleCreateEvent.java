package com.forguta.libs.saga.auto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleCreateEvent implements Serializable {

    private SampleDto sampleDto;
}
