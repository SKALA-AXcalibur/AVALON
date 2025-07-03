package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 매핑 항목
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMappingItem {
    
    @JsonProperty("scenarioId")
    private String scenarioId;
    
    @JsonProperty("stepName")
    private String stepName;
    
    @JsonProperty("apiName")
    private String apiName;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("parameters")
    private Map<String, Object> parameters;
    
    @JsonProperty("responseStructure")
    private Map<String, Object> responseStructure;
}