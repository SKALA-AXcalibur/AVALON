package com.sk.skala.axcalibur.apitest.feature.dto.request;

import org.springframework.util.MultiValueMap;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

/**
 * API 요청/응답 데이터를 담는 DTO
 * 파라미터와 테스트케이스 데이터를 결합하여 생성된 실제 API 요청/응답 정보
 */
@Builder(toBuilder = true)
@Schema(description = "API 요청/응답 데이터 DTO")
public record ApiRequestDataDto(
                @NotNull @Schema(description = "요청 헤더 정보") MultiValueMap<String, String> reqHeader,
                @NotNull @Schema(description = "요청 쿼리 파라미터 정보") MultiValueMap<String, String> reqQuery,
                @NotNull @Schema(description = "요청 본문 정보") Map<String, Object> reqBody,
                @NotNull @Schema(description = "요청 경로 변수 정보") Map<String, String> reqPath,
                @NotNull @Schema(description = "예상 응답 헤더 정보") MultiValueMap<String, String> resHeader,
                @NotNull @Schema(description = "예상 응답 본문 정보") Map<String, Object> resBody) {
}
