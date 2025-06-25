package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioListResponse {
    
    private String scenarioId;          // 시나리오 아이디
    private String name;                // 시나리오 이름
} 