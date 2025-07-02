package com.sk.skala.axcalibur.feature.apilist.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDto {
    private String scenarioId;  // 시나리오 ID
    private String title;       // 시나리오 제목
    private String description; // 시나리오 설명
    private String validation;  // 시나리오 검증 여부
}
