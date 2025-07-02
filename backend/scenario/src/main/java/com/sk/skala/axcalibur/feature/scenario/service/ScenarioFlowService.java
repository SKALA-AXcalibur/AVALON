package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioFlowResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCUResponseDto;


public interface ScenarioFlowService {
    
    /**
     * 프로젝트의 모든 시나리오 데이터 준비 (매핑표 기반)
     * @param projectKey 프로젝트 키
     * @return AI 서비스에 보낼 요청 DTO
     */
    ScenarioFlowRequestDto prepareAllScenariosFlowData(Integer projectKey);
    
    /**
     * 개별 시나리오 데이터 준비 (매핑표 기반)
     * @param result 시나리오 생성/수정 응답 DTO
     * @return AI 서비스에 보낼 요청 DTO
     */
    ScenarioFlowRequestDto prepareSingleScenarioFlowData(ScenarioCUResponseDto result);
    
    /**
     * 프로젝트의 모든 시나리오에 대한 플로우차트 생성 (시나리오 생성단용)
     * @param projectKey 프로젝트 키
     * @return 생성된 플로우차트 데이터
     */
    ScenarioFlowResponseDto generateFlowchartForAllScenarios(Integer projectKey);
    
    /**
     * 개별 시나리오에 대한 플로우차트 생성 (시나리오 추가/수정용)
     * @param result 시나리오 생성/수정 응답 DTO
     * @return 생성된 플로우차트 데이터
     */
    ScenarioFlowResponseDto generateFlowchartForSingleScenario(ScenarioCUResponseDto result);
} 