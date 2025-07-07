package com.sk.skala.axcalibur.apitest.feature.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "액션 정보를 담는 레코드")
public record ApiTestParserActionInfoDto(
    @NotNull @Schema(description = "소스") ApiTestParserSourceTargetDto source,
    @NotNull @Schema(description = "타겟") ApiTestParserSourceTargetDto target

) {

}
