package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;

/**
 * 시나리오 목록 조회 서비스 인터페이스(IF-SN-0009)
 */
public interface ScenarioListService {
    
    /**
     * 프로젝트별 시나리오 목록 조회
     * @param projectKey 프로젝트 키
     * @return 시나리오 목록 응답 DTO
     */
    ScenarioListDto getScenarioList(Integer projectKey);
} 