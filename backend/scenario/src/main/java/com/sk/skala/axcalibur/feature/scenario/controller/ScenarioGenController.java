package com.sk.skala.axcalibur.feature.scenario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;

public interface ScenarioGenController {
    @PostMapping("/scenario/v1/create")
    ResponseEntity<ScenarioGenResponseDto> generateScenario(
        @CookieValue("avalon") String key);
}
