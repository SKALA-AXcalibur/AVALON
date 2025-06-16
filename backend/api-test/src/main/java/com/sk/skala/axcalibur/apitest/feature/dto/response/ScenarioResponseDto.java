package com.sk.skala.axcalibur.apitest.feature.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ScenarioResponseDto(
    @NotBlank
    String scenarioId,
    @NotBlank
    String scenarioName,
    Boolean isSuccess
) {

}
