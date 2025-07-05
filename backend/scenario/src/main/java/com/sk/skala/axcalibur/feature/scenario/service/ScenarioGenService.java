package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ReqItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ScenarioItem;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

/**
 * 시나리오 생성 서비스 인터페이스
 */
public interface ScenarioGenService {
    
    /**
     * 시나리오 생성 요청 데이터 준비
     * @param projectKey 프로젝트 키
     * @return 시나리오 생성 요청 DTO
     */
    ScenarioGenRequestDto prepareRequestData(Integer projectKey);
    
    /**
     * FastAPI 응답을 파싱하여 시나리오 엔티티로 변환하고 DB에 저장
     * @param scenarioList FastAPI에서 받은 시나리오 리스트
     * @param projectKey 프로젝트 키
     * @return 저장된 시나리오 엔티티 리스트
     */
    List<ScenarioEntity> parseAndSaveScenarios(List<ScenarioItem> scenarioList, Integer projectKey);
    
    /**
     * 요구사항 정보 수집
     * @param projectKey 프로젝트 키
     * @return 요구사항 아이템 리스트
     */
    List<ReqItem> collectRequirements(Integer projectKey);
    
    /**
     * API 정보 수집
     * @param projectKey 프로젝트 키
     * @return API 아이템 리스트
     */
    List<ApiItem> collectApiList(Integer projectKey);
    
    /**
     * 프로젝트 키를 기준으로 시나리오 데이터 삭제
     * @param projectKey 프로젝트 키
     * @return 삭제된 시나리오 데이터 개수
     */
    long deleteScenariosByProjectKey(Integer projectKey);
}
