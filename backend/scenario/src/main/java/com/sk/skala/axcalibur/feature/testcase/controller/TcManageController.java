package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcListResponse;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * 테스트케이스 관련 조회/삭제/수정 인터페이스
 * - 시나리오 ID를 입력받아 해당 시나리오로부터 생성된 TC 리스트를 조회하는 파트를 구현합니다.
 * - TC ID를 입력받아 조회/수정/삭제를 구현합니다.
 */
public interface TcManageController {
    ResponseEntity<SuccessResponse<TcListResponse>> getTestcaseLists(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int query
    );

    ResponseEntity<SuccessResponse<TcDetailResponse>> getTestcases(
        @PathVariable String tcId,
        @CookieValue("avalon") String key
    );

    ResponseEntity<SuccessResponse<Void>> deleteTestcase(
            @PathVariable String tcId,
            @CookieValue("avalon") String key
    );

    ResponseEntity<SuccessResponse<Void>> updateTestcase(
            @PathVariable String tcId,
            @CookieValue("avalon") String key,
            @RequestBody TcUpdateRequest request
    );
}
