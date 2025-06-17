package com.sk.skala.axcalibur.apitest.feature.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetTestResultServiceRequestDto(
    @NotNull
    Integer projectKey,
    @Nullable
    String cursor,
    @Nullable
    Integer size
) {

}
