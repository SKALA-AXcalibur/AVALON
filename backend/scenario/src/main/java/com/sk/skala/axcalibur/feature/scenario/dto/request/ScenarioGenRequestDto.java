package com.sk.skala.axcalibur.feature.scenario.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 시나리오 생성 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioGenRequestDto {
    
    @JsonProperty("project_id")
    private String projectId;
    private List<ReqItem> requirement;
    @JsonProperty("api_list")
    private List<ApiItem> apiList;
    
}