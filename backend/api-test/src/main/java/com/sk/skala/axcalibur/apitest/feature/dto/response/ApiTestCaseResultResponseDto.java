package com.sk.skala.axcalibur.apitest.feature.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ApiTestCaseResultResponseDto(
    @NotBlank
    String scenarioId,
    @NotBlank
    String scenarioName,
    @NotNull
    List<TestcaseInfoResponseDto> tcList
) {

}
