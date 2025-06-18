package com.sk.skala.axcalibur.apitest.feature.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ApiTestResultResponseDto(
    
    @NotNull @Schema(description = "시나리오 리스트") List<ScenarioResponseDto> scenarioList
) {

}
