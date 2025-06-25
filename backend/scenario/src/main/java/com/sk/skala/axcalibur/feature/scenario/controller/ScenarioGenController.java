package com.sk.skala.axcalibur.feature.scenario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;

public interface ScenarioGenController {

    @Operation(summary = "시나리오 생성 요청", description = "시나리오 생성을 시작합니다.")
    ResponseEntity<SuccessResponse<ScenarioGenResponseDto>> generateScenario(
        @CookieValue("avalon") String key);
}
