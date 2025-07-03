package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioListResponse {
    
    private String scenarioId;    // 시나리오 ID
    private String name;          // 시나리오 이름
}