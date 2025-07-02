package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioUpdateDto {
    private String scenarioId;  // 수정된 시나리오 ID
    
    public String getScenarioId() {
        return scenarioId;
    }
}
