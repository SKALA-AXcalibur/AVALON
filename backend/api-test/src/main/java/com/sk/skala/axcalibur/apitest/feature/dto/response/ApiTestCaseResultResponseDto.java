package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ApiTestCaseResultResponseDto(
    @NotBlank
    @Schema(description = "시나리오 ID")
    String scenarioId,
    @NotBlank
    @Schema(description = "시나리오 이름")
    String scenarioName,
    @NotNull
    @Schema(description = "테스트케이스 정보 리스트")
    List<TestcaseInfoResponseDto> tcList
) {

}
