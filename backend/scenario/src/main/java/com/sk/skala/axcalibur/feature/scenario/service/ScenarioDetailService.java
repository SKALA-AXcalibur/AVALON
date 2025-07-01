package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDetailResponseDto;

/**
 * 시나리오 상세 조회 서비스 인터페이스(IF-SN-0008)
 */
public interface ScenarioDetailService {
    
    /**
     * 시나리오 상세 조회
     * @param projectKey 프로젝트 키
     * @param scenarioId 조회할 시나리오 ID
     * @return 시나리오 상세 정보
     */
    
    ScenarioDetailResponseDto getScenarioDetail(Integer projectKey, String scenarioId);
} 