package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ExcuteTestServiceRequestDto(
    @NotNull
    @Schema(description = "시나리오 리스트")
    List<String> scenarioList,
    @NotNull
    @Schema(description = "프로젝트 pk")
    Integer projectKey
) {

}
