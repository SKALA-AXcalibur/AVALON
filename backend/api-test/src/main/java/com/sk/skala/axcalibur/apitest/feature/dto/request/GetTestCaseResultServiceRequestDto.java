package com.sk.skala.axcalibur.apitest.feature.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetTestCaseResultServiceRequestDto(
    @NotNull
    Integer projectKey,
    @NotBlank
    String scenarioId,
    String cursor,
    Integer size
) {

}
