package com.sk.skala.axcalibur.feature.scenario.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 흐름도 API 요청 DTO
 * FastAPI의 /api/scenario/v1/scenario 엔드포인트 요청 스펙과 100% 매핑
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioFlowRequestDto {
    
    @JsonProperty("scenario_list")
    private List<ScenarioFlowItem> scenarioList;
} 