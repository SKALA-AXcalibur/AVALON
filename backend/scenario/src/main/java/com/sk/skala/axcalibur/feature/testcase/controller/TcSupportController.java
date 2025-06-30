package com.sk.skala.axcalibur.feature.testcase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

/**
 * 테스트케이스 추가 관련 인터페이스(IF-TC-0007 ~ IF-TC-0010)
 * - 시나리오 ID로부터 API 목록을 조회하고, 선택한 API의 파라미터 정보를 활용해 TC를 추가합니다.
 */
public interface TcSupportController {
    ResponseEntity<SuccessResponse<List<ApiListResponse>>> getApiListByScenario(String scenarioId, String key);

    ResponseEntity<SuccessResponse<List<ApiParamDto>>> getParamListByApi(String scenarioId, String apiId, String key);

    ResponseEntity<SuccessResponse<Void>> createTestcase(String scenarioId, String apiId, String key, TcUpdateRequest request);
}
