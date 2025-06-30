package com.sk.skala.axcalibur.feature.scenario.service;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;

/**
 * 시나리오 목록 조회 서비스 인터페이스
 */
public interface ScenarioListService {
    
    /**
     * 프로젝트별 시나리오 목록 조회
     * @param projectKey 프로젝트 키
     * @param offset 조회 시작점
     * @param query 조회 개수
     * @return 시나리오 목록 응답 DTO
     */
    ScenarioListDto getScenarioList(Integer projectKey, int offset, int query);

    /**
     * offset, query 파라미터 검증
     */
    void validatePagingParameters(int offset, int query);
} 