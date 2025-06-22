package com.sk.skala.axcalibur.feature.scenario.dto.response;

import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 시나리오 생성 요청 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioGenResponseDto {
    
    // 시나리오 리스트
    private List<ScenarioListResponse> scenarioList;

    // 총 시나리오 개수
    private int total;
} 
