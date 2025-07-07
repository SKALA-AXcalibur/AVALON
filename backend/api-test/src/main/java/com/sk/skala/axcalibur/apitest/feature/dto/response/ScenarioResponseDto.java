package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ScenarioResponseDto(
    @NotBlank
    @Schema(description = "시나리오 ID")
    String scenarioId,
    @NotBlank
    @Schema(description = "시나리오 이름")
    String scenarioName,
    @NotBlank
    @Schema(description = "시나리오 테스트 성공 여부")
    String isSuccess
) {

}
