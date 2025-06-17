package com.sk.skala.axcalibur.apitest.feature.dto.response;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TestcaseInfoResponseDto(
    @NotBlank
    String tcId,
    @NotBlank
    String description,
    @NotBlank
    String expectedResult,
    @NotBlank
    String isSuccess,
    LocalDateTime excutedTime
) {

}
