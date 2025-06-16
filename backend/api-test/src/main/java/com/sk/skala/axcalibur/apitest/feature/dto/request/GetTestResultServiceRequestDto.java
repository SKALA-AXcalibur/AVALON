package com.sk.skala.axcalibur.apitest.feature.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetTestResultServiceRequestDto(
    @NotNull
    Integer projectKey,
    String cursor,
    Integer size
) {

}
