package com.sk.skala.axcalibur.feature.scenario.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 생성 요청 DTO
 * IF-SN-0003 시나리오 생성
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioCreateRequestDto {
    private String name; // 시나리오 이름
    private String description; // 시나리오 설명
    private String validation; // 검증 포인트
} 