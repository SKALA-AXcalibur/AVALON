package com.sk.skala.axcalibur.feature.scenario.dto.response;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListItem;

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
    private List<ScenarioListItem> scenarioList;
    private int total;
} 