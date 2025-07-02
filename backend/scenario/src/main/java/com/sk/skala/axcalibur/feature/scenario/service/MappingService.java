package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.request.MappingRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.MappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCUResponseDto;

public interface MappingService {
    
    /**
     * 프로젝트의 모든 시나리오 매핑 데이터 준비
     * @param projectKey 프로젝트 키
     * @return AI 서비스에 보낼 요청 DTO
     */
    MappingRequestDto prepareAllMappingData(Integer projectKey);
    
    /**
     * 개별 시나리오 매핑 데이터 준비
     * @param scenarioKey Primary Key
     * @return AI 서비스에 보낼 요청 DTO
     */
    MappingRequestDto prepareSingleMappingData(ScenarioCUResponseDto result);
    
    /**
     * 프로젝트의 모든 시나리오에 대한 매핑 생성 (시나리오 생성단용)
     * @param projectKey 프로젝트 키
     * @return 생성된 매핑 데이터
     */
    MappingResponseDto generateMappingForAllScenarios(Integer projectKey);
    
    /**
     * 개별 시나리오에 대한 매핑 생성 (시나리오 추가/수정용)
     * @param scenarioKey Primary Key
     * @return 생성된 매핑 데이터
     */
    MappingResponseDto generateMappingForSingleScenario(ScenarioCUResponseDto result);
}
