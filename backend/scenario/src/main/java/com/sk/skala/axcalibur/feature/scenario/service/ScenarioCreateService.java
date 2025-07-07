package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

/**
 * 시나리오 추가 서비스 인터페이스
 */
public interface ScenarioCreateService {
    
    /**
     * 시나리오 추가(IF-SN-0003)
     * @param projectKey 프로젝트 키
     * @param requestDto 시나리오 생성 요청 DTO
     * @return 시나리오 생성 응답 DTO
     */
    SuccessResponse<ScenarioCreateResponseDto> createScenario(Integer projectKey, ScenarioCreateRequestDto requestDto);

} 