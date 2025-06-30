package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcListResponse;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

/**
 * 테스트케이스 관련 조회/삭제/수정 인터페이스(IF-TC-0003 ~ IF-TC-0010)
 * - 시나리오 ID를 입력받아 해당 시나리오로부터 생성된 TC 리스트를 조회하는 파트를 구현합니다.
 * - TC ID를 입력받아 조회/수정/삭제/추가를 구현합니다.
 */
public interface TcManageController {
    ResponseEntity<SuccessResponse<TcListResponse>> getTestcaseLists(String scenarioId, String key, Pageable pageable);

    ResponseEntity<SuccessResponse<TcDetailResponse>> getTestcases(String tcId, String key);

    ResponseEntity<SuccessResponse<Void>> updateTestcase(String tcId, String key, TcUpdateRequest request);

    ResponseEntity<SuccessResponse<Void>> deleteTestcase(String tcId, String key);

    ResponseEntity<SuccessResponse<String>> addTestcase(String scenarioId, String apiId, String key, TcUpdateRequest request);
}
