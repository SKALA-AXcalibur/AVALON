package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "테스트 케이스 결과 이유 DTO")
public record TestcaseResultUpdateReasonDto(
        @NotBlank @Schema(description = "이유") String reason,
        @NotNull @Schema(description = "성공 여부") Boolean success) {

}
