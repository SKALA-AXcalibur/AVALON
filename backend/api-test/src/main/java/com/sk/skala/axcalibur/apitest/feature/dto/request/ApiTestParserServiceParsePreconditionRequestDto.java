package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "API 테스트 파서 서비스 사전 조건 파싱 요청 DTO")
public record ApiTestParserServiceParsePreconditionRequestDto(
        @NotBlank @Schema(description = "사전 조건 문자열") String precondition,
        @NotNull @Schema(description = "시나리오 키") Integer scenarioKey,
        @NotNull @Schema(description = "응답코드") Integer statusCode) {

}
