package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.response.GetScenarioServiceResponseDto;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

  private final ScenarioRepository repo;

  /**
   * 시나리오 ID로 시나리오 정보를 조회합니다.
   *
   * @param scenarioId 조회할 시나리오의 ID
   * @return 시나리오 정보가 담긴 GetScenarioServiceResponseDto 객체
   * @throws BusinessExceptionHandler 시나리오가 존재하지 않을 경우 예외 발생
   */
  @Override
  public GetScenarioServiceResponseDto getScenarioService(String scenarioId) {
    log.info("ScenarioServiceImpl.getScenarioService() called with scenarioId: {}", scenarioId);
    var entity = repo.findByScenarioId(scenarioId).orElseThrow(() -> {
      log.error("Scenario not found, ID: {}", scenarioId);
      return new BusinessExceptionHandler("Scenario not found", ErrorCode.SCENARIO_NOT_FOUND_ERROR);
    });

    return GetScenarioServiceResponseDto.builder()
        .scenarioId(entity.getScenarioId())
        .scenarioName(entity.getName())
        .build();
  }
}
