package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;

/**
 * 시나리오 생성 서비스 인터페이스
 */
public interface ScenarioCreateService {
    
    /**
     * 시나리오 생성
     * @param projectKey 프로젝트 키
     * @param requestDto 시나리오 생성 요청 DTO
     * @return 시나리오 생성 응답 DTO
     */
    ScenarioCreateResponseDto createScenario(Integer projectKey, ScenarioCreateRequestDto requestDto);
    
    /**
     * 새로운 시나리오 ID 생성
     * @return 생성된 시나리오 ID
     */
    String generateNewScenarioId();
} 