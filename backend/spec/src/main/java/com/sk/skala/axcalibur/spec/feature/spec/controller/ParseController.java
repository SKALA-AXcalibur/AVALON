package com.sk.skala.axcalibur.spec.feature.spec.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import com.sk.skala.axcalibur.spec.feature.spec.dto.EmptyResponseDto;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 명세서 분석 컨트롤러
 * - '명세서 분석(IF-SP-0004)'
 * @param key 인증용 Cookie 문자열
 * @return 업로드 결과 반환
 * - 요청 데이터는 유효성 검사를 거쳐 서비스에 전달
 * - 예외 발생 시 global.exception.GlobalExceptionHandler 에서 일괄 처리 
 */
public interface ParseController {

    @Operation(summary = "명세서 분석 요청", description = "명세서 분석을 시작합니다.")
    ResponseEntity<SuccessResponse<EmptyResponseDto>> analyzeSpec(
        @CookieValue("avalon") String key

    );
        
} 