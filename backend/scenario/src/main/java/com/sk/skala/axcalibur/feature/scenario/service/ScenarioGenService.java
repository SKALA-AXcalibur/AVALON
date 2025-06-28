package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListResponse;

public interface ScenarioGenService {
    
    // DB에서 프로젝트 관련 정보 수집하여 FastAPI 요청 데이터 준비
    ScenarioGenRequestDto prepareRequestData(Integer projectKey);
    
    // FastAPI 응답을 파싱해서 DB에 저장하고 응답 DTO 반환
    List<ScenarioListResponse> parseAndSaveScenarios(List<ScenarioListResponse> scenarioList, Integer projectKey);
    
    // 요구사항 정보 수집
    List<ReqItem> collectRequirements(Integer projectKey); 

    // API 정보 수집
    List<ApiItem> collectApiList(Integer projectKey); 

    // 새로운 시나리오 ID 생성
    String generateNewScenarioId(); 
    
    // 저장된 시나리오에서 흐름도 생성 요청 데이터 준비
    ScenarioFlowRequestDto prepareFlowRequestData(List<ScenarioListResponse> savedScenarios, Integer projectKey);
    
    // 시나리오 흐름도 생성 및 DB 업데이트
    void generateAndUpdateFlowChart(List<ScenarioListResponse> savedScenarios, Integer projectKey);
}
