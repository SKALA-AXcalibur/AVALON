package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiParamListResponse;

/**
 * TC 수동 추가 파트 중 보조 기능 구현체
 * - 시나리오 ID로부터 API 목록을 반환합니다.
 * - 선택한 API ID로부터 해당 API의 파라미터 목록을 반환합니다.
 */
public interface SupportQueryService {
    ApiListResponse getApiListByScenario(String scenarioId);
    
    ApiParamListResponse getParamsByApiId(String apiId);
}
