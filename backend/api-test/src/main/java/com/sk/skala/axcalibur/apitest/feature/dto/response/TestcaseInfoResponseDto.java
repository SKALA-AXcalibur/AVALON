package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TestcaseInfoResponseDto(
    @NotBlank
    @Schema(description = "테스트케이스 ID")
    String tcId,
    @NotBlank
    @Schema(description = "테스트케이스 설명")
    String description,
    @NotBlank
    @Schema(description = "테스트케이스 예상 결과")
    String expectedResult,
    @NotBlank
    @Schema(description = "테스트케이스 성공 여무")
    String isSuccess,
    @Nullable
    @Schema(description = "테스트케이스 수행 시간")
    LocalDateTime excutedTime
) {

}
