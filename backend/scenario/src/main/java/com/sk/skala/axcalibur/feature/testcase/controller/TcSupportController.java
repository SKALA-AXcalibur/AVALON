package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.http.ResponseEntity;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiParamListResponse;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

/**
 * 테스트케이스 추가 전 보조 정보 조회 인터페이스(IF-TC-0007, IF-TC-0008)
 * - 테스트케이스를 수동으로 추가하기 전, 시나리오 기반으로 필요한 정보를 조회합니다.
 * - API 조회(IF-TC-0007)
 * - API 선택(IF-TC-0008)
 */
public interface TcSupportController {
    ResponseEntity<SuccessResponse<ApiListResponse>> getApiListByScenario(String scenarioId, String key);

    ResponseEntity<SuccessResponse<ApiParamListResponse>> getParamListByApi(String scenarioId, String apiId, String key);
}
