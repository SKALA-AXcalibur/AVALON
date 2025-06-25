package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 시나리오 생성 요청 응답 DTO (IF-TC-0001)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioGenResponseDto {
    
    private List<ScenarioListResponse> scenarioList; // 시나리오 리스트
    private int total;                 // 총 시나리오 개수
} 
