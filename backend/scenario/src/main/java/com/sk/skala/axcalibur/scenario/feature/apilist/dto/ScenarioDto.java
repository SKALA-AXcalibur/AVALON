package com.sk.skala.axcalibur.scenario.feature.apilist.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDto {
    private String scenarioId;
    private String title;
    private String description;  // 시나리오 설명
    private String validation;
}
