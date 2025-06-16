package com.sk.skala.axcalibur.apitest.feature.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record ExcuteTestServiceRequestDto(
    @NotNull
    List<String> scenarioList,
    @NotNull
    Integer projectKey
) {

}
