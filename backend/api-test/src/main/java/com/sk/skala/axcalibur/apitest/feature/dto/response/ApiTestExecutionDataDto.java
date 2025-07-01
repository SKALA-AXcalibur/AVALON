package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * API 테스트 실행에 필요한 모든 데이터를 담는 DTO
 */
@Builder(toBuilder = true)
@Schema(description = "API 테스트 실행 데이터 DTO")
public record ApiTestExecutionDataDto(
        @NotNull @Schema(description = "매핑 ID") Integer mappingId,
        @NotNull @Schema(description = "매핑 단계") Integer step,
        @NotNull @Schema(description = "테스트케이스 ID") Integer testcaseId,
        @NotNull @Schema(description = "테스트케이스 문자열 ID") String testcaseStringId,
        @NotNull @Schema(description = "테스트케이스 전제조건") String precondition,
        @NotNull @Schema(description = "테스트케이스 상태코드") Integer status,
        @NotNull @Schema(description = "API 리스트 ID") Integer apiListId,
        @NotNull @Schema(description = "API 메서드") String method,
        @NotNull @Schema(description = "API URL") String url,
        @NotNull @Schema(description = "API 경로") String path) {
}
