package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "스텝 정보를 담는 레코드")
public record ApiTestParserStepInfoDto(
    @NotNull @Schema(description = "API 단계") Integer step,
    @NotBlank @Schema(description = "위치, 키") String action) {

}
