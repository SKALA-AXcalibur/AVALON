package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingScenarioItem {  
    private String scenarioId;  // 시나리오 ID
    private String title;       // 시나리오 제목
    private String description; // 시나리오 설명
    private String validation;  // 시나리오 검증 여부
}
