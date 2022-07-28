package com.forguta.libs.saga.auto;

import com.forguta.libs.saga.core.model.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SampleDto implements IDto {
    private String data;
}
