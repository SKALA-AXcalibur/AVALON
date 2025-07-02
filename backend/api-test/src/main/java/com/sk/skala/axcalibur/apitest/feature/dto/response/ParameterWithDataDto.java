package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 파라미터와 테스트케이스 데이터를 함께 담는 DTO
 */
@Builder(toBuilder = true)
@Schema(description = "파라미터와 테스트케이스 데이터 DTO")
public record ParameterWithDataDto(
        @NotNull @Schema(description = "파라미터 ID") Integer parameterId,
        @NotNull @Schema(description = "파라미터 이름") String parameterName,
        @NotNull @Schema(description = "파라미터 데이터 타입") String dataType,
        @NotNull @Schema(description = "API 목록 ID") Integer apiListId,
        @NotNull @Schema(description = "카테고리 이름") String categoryName,
        @NotNull @Schema(description = "컨텍스트 이름") String contextName,
        @Schema(description = "부모 파라미터 ID") Integer parentId,
        @Schema(description = "테스트케이스 ID") Integer testcaseId,
        @Schema(description = "테스트케이스 데이터 값") String value) {
}
