package com.sk.skala.axcalibur.apitest.feature.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GetScenarioServiceResponseDto(
    @NotBlank
    String scenarioId,
    @NotBlank
    String scenarioName
) {

}
