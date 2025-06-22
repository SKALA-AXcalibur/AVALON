package com.sk.skala.axcalibur.feature.scenario.dto.response.item;

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
    
    // 시나리오 아이디
    private String id;
    
    // 시나리오 이름
    private String name;
} 