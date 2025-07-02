package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowApiItem {
    
    private String id;                       // API 아이디
    private String name;                     // API 이름
    private String description;              // API 설명
} 