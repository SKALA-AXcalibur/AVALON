package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioListResponse;

public interface ScenarioGenService {
    
    /**
     * DB에서 프로젝트 관련 정보 수집하여 FastAPI 요청 데이터 준비
     * @param projectKey 프로젝트 키
     * @return FastAPI로 전송할 요청 데이터
     */
    ScenarioGenRequestDto prepareRequestData(Integer projectKey);
    
    /**
     * FastAPI 응답을 파싱해서 DB에 저장하고 응답 DTO 반환
     * @param fastApiResponse FastAPI JSON 응답
     * @param projectKey 프로젝트 키
     * @return 시나리오 응답 목록
     */
    List<ScenarioListResponse> parseAndSaveScenarios(String fastApiResponse, Integer projectKey);
}
