package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 상세 조회 응답 DTO
 * IF-SN-0008 시나리오 조회
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioDetailResponseDto {
    private String id; // 시나리오 식별자
    private String name; // 시나리오 이름
    private String graph; // 시나리오 흐름도
    private String description; // 시나리오 설명
    private String validation; // 검증 포인트
} 