package com.sk.skala.axcalibur.apitest.feature.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "API 테스트 실행 결과 응답 DTO")
public record ExcuteApiTestResponseDto(
        @NotNull @Schema(description = "테스트 케이스 실행 아이디 목록") List<String> testcaseIdList

) {

}
