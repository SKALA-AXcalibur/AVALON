package com.sk.skala.axcalibur.apitest.feature.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
public record ExcuteApiTestRequestDto(
    @NotEmpty
    @Schema(description = "시나리오 리스트")
    List<String> scenarioList
) {

}
