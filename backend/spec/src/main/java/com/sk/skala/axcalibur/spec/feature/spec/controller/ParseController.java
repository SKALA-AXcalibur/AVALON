package com.sk.skala.axcalibur.spec.feature.spec.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

public interface ParseController {

    @Operation(summary = "명세서 분석 요청", description = "명세서 분석을 시작합니다.")
    ResponseEntity<SuccessResponse<List<Object>>> analyzeSpec(
        @CookieValue("avalon") String key

    );
        
} 