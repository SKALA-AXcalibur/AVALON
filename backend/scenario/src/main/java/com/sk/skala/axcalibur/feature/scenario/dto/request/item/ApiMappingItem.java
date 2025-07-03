package com.sk.skala.axcalibur.feature.scenario.dto.request.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI ApiInput 스펙과 매핑되는 API 매핑 아이템
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMappingItem {
    
    @JsonProperty("apiName")
    private String apiName;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("parameters")
    private String parameters;
    
    @JsonProperty("responseStructure")
    private String responseStructure;
} 