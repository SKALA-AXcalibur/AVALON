package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.response.GetScenarioServiceResponseDto;

public interface ScenarioService {
  GetScenarioServiceResponseDto getScenarioService(String scenarioId);
}
