package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI ScenarioItem 스펙과 매핑되는 시나리오 플로우 아이템
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowItem {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("api_list")
    private List<ApiFlowItem> apiList;
} 