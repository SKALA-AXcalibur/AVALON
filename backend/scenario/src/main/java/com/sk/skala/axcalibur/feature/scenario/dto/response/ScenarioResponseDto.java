package com.sk.skala.axcalibur.feature.scenario.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FastAPI 시나리오 생성 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioResponseDto {
    
    @JsonProperty("scenario_list")
    private List<ScenarioItem> scenarioList;
} 