package com.sk.skala.axcalibur.apitest.feature.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetTestCaseResultServiceRequestDto(
    @NotNull
    @Schema(description = "프로젝트 pk")
    Integer projectKey,
    @NotBlank
    @Schema(description = "시나리오 ID")
    String scenarioId,
    @Schema(description = "조회 시작할 테스트케이스 ID")
    String cursor,
    @Schema(description = "조회할 테스트케이스 개수")
    Integer size
) {

}
