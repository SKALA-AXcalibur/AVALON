package com.sk.skala.axcalibur.feature.scenario.dto.response;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 목록 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioListDto {
    private List<ScenarioItem> scenarioList;
    private int total;
} 