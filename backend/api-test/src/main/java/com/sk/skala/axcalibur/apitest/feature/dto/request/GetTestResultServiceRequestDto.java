package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetTestResultServiceRequestDto(
    @NotNull
    @Schema(description = "프로젝트 pk")
    Integer projectKey,
    @Nullable
    @Schema(description = "조회 시작할 시나리오 아이디")
    String cursor,
    @Nullable
    @Schema(description = "조회할 시나리오 개수")
    Integer size
) {

}
