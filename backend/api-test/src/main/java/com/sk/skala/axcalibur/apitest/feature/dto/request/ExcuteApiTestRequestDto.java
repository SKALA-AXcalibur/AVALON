package com.sk.skala.axcalibur.apitest.feature.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
public record ExcuteApiTestRequestDto(
    @NotEmpty
    List<String> scenarioList
) {

}
