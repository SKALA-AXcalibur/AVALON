package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TestcaseSuccessResponseDto(
    @NotNull
    @Schema(description = "테스트케이스 pk")
    Integer key,
    @NotBlank
    @Schema(description = "시나리오 ID")
    String scenarioId,
    @Schema(description = "테스트케이스 성공 여부")
    Boolean success
) {

}
