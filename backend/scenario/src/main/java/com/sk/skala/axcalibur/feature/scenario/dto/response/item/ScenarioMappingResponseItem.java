package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI ScenarioItem 응답 스펙과 매핑되는 시나리오 매핑 응답 아이템
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioMappingResponseItem {
    
    @JsonProperty("scenarioId")
    private String scenarioId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("validation")
    private String validation;
} 