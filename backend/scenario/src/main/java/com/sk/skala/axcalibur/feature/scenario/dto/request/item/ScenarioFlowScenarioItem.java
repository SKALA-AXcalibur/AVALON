package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowScenarioItem {
    
    private String id;                                    // 시나리오 아이디
    private String description;                           // 시나리오 설명
    private List<ScenarioFlowApiItem> apiList;           // API 목록
} 