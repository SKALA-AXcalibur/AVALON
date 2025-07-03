
package com.sk.skala.axcalibur.feature.scenario.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiMappingItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioMappingItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 매핑 API 요청 DTO
 * FastAPI의 /api/list/v1/create 엔드포인트 요청 스펙과 100% 매핑
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMappingRequestDto {
    
    @JsonProperty("apiList")
    private List<ApiMappingItem> apiList;
    
    @JsonProperty("scenarioList")
    private List<ScenarioMappingItem> scenarioList;
} 