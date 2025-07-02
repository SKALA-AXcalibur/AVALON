package com.sk.skala.axcalibur.feature.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 생성 응답 DTO
 * IF-SN-0003 시나리오 생성, 시나리오 수정 
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioCUResponseDto {
    private String id; // 생성된 시나리오 식별자
    
    public String getId() {
        return id;
    }
}