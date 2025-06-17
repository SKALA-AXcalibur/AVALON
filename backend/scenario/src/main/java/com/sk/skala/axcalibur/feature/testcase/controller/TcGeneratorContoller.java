package com.sk.skala.axcalibur.feature.testcase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import com.sk.skala.axcalibur.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 테스트케이스 생성 요청 인터페이스
 * '테스트케이스 생성 요청(IF-TC-0001)'을 구현합니다.
 */

public interface TcGeneratorContoller {
    @Operation(summary = "테스트케이스 생성", description = "프론트로부터 요청받아 생성을 처리합니다.")
    ResponseEntity<SuccessResponse<List<Object>>> generateTestCases(@CookieValue("avalon") String key);
}
