package com.sk.skala.axcalibur.feature.testcase.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 시나리오 정보 DTO
 * 전달하려는 시나리오의 이름, 검증포인트 등에 대한 정보 정의
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDto {
    private String scenarioId;
    private String scenarioName;
    private String scenarioDesc;
    private String validation;
}
