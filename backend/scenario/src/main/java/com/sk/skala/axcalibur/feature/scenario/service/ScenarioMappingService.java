package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ApiMappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioFlowResponseDto;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

/**
 * 시나리오 매핑 및 흐름도 생성 서비스 인터페이스
 */
public interface ScenarioMappingService {
    
    /**
     * 특정 시나리오에 대한 매핑 생성 및 저장
     * @param scenarioId 대상 시나리오 ID
     * @return 매핑 생성 결과
     */
    ApiMappingResponseDto generateAndSaveMapping(String scenarioId);
    
    /**
     * 특정 시나리오에 대한 흐름도 생성 (FastAPI 호출만, 저장은 FastAPI에서 처리)
     * @param scenarioId 대상 시나리오 ID
     * @return 흐름도 생성 결과
     */
    ScenarioFlowResponseDto generateFlowchart(String scenarioId);
    
    /**
     * 여러 시나리오에 대한 매핑 생성 및 저장 (시나리오 생성 시 사용)
     * @param scenarios 대상 시나리오 목록
     * @param projectKey 프로젝트 키
     * @return 매핑 생성 결과
     */
    ApiMappingResponseDto generateAndSaveMappingForScenarios(Integer projectKey, List<ScenarioEntity> scenarios);
    
    /**
     * 여러 시나리오에 대한 흐름도 생성 (시나리오 생성 시 사용)
     * @param scenarios 대상 시나리오 목록
     * @param projectKey 프로젝트 키
     * @return 흐름도 생성 결과
     */
    ScenarioFlowResponseDto generateFlowchartForScenarios(Integer projectKey, List<ScenarioEntity> scenarios);
} 