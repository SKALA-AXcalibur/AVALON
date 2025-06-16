package com.sk.skala.axcalibur.apitest.feature.dto.response;


import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ApiTestResultResponseDto(
    @NotNull List<ScenarioResponseDto> scenarioList
) {

}
