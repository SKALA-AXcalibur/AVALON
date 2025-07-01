package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;

/**
 * 시나리오 수정 서비스 인터페이스(IF-SN-0004)
 */
public interface ScenarioUpdateService {
    
    /**
     * 시나리오 수정
     * @param projectKey 프로젝트 키
     * @param scenarioId 수정할 시나리오 ID
     * @param requestDto 시나리오 수정 요청 DTO
     */
    void updateScenario(Integer projectKey, String scenarioId, ScenarioUpdateRequestDto requestDto);
} 