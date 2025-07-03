package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI ApiItem 스펙과 매핑되는 API 플로우 아이템
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiFlowItem {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
} 