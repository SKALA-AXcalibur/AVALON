package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "소스/타겟 정보를 담는 레코드")
public record ApiTestParserSourceTargetDto(
    @NotBlank @Schema(description = "카테고리 타입") String type,
    @NotBlank @Schema(description = "키") String key) {

}
