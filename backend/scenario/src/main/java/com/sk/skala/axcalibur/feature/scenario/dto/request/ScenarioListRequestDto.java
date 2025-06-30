package com.sk.skala.axcalibur.feature.scenario.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 시나리오 목록 조회 요청 DTO
 * IF-SN-0009 시나리오 목록 조회
 */
@Getter
@AllArgsConstructor
@Builder
public class ScenarioListRequestDto {
    private int offset = 0; // 조회 시작 위치
    private int query = 10; // 조회 개수
}
