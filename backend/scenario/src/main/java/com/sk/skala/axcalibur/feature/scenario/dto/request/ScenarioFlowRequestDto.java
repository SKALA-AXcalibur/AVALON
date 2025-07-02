package com.sk.skala.axcalibur.feature.scenario.dto.request;

import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowScenarioItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowRequestDto {
    
    private List<ScenarioFlowScenarioItem> scenarioList;    // 시나리오 목록
} 