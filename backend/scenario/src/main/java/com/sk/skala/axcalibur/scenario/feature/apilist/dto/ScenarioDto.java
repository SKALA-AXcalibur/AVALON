package com.sk.skala.axcalibur.scenario.apilist.feature.dto;

import lombok.Getter;

@Getter
public class ScenarioDto {
    private String scenarioId;
    private String title;
    private String description;  // 시나리오 설명
    private String validation;
}
