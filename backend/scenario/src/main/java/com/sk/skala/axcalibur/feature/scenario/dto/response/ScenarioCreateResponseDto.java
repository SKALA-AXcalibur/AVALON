package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 추가 응답 DTO
 * IF-SN-0003 시나리오 추가
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioCreateResponseDto {
    private String id; // 생성된 시나리오 식별자
} 