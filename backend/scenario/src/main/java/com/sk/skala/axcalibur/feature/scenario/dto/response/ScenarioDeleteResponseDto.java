package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 삭제 응답 DTO
 * IF-SN-0007 시나리오 삭제
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioDeleteResponseDto {
    private String id; // 삭제된 시나리오 아이디
} 