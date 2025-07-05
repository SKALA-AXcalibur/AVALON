package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ApiItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioItem {
    
    @JsonProperty("scenario_id")
    private String scenarioId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("validation")
    private String validation;
    
    @JsonProperty("api_list")
    private List<ApiItem> apiList;
    
    // 기존 코드와의 호환성을 위한 getter
    public String getId() {
        return scenarioId;
    }
    
    public String getName() {
        return title; // title을 name으로도 사용
    }
}