package com.sk.skala.axcalibur.apitest.feature.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TestcaseSuccessResponseDto(
    @NotNull
    Integer key,
    @NotBlank
    String scenarioId,
    Boolean success
) {

}
