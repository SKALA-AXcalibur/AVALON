package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDeleteResponseDto;

/**
 * 시나리오 삭제 서비스 인터페이스
 */
public interface ScenarioDeleteService {
    
    /**
     * 시나리오 삭제
     * @param projectKey 프로젝트 키
     * @param scenarioId 삭제할 시나리오 ID
     * @return 시나리오 삭제 응답 DTO
     */
    ScenarioDeleteResponseDto deleteScenario(Integer projectKey, String scenarioId);
} 