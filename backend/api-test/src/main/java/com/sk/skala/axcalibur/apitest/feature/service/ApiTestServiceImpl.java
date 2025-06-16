package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTestServiceImpl implements ApiTestService{

  private final TestcaseResultRepository repo;
  private final TestcaseRepository tc;
  private final ScenarioRepository scene;


  @Override
  public List<String> excuteTestService(ExcuteTestServiceRequestDto dto) {
    log.info("ApiTestServiceImpl.excuteTestService() called with dto size: {}", dto.scenarioList()
        .size());
    var list = dto.scenarioList();
    if (list.isEmpty()) {
      log.warn("No scenarios provided for execution.");
      return List.of();
    }

    return List.of();
  }

  @Override
  public List<ScenarioResponseDto> getTestResultService(GetTestResultServiceRequestDto dto) {
    log.info("ApiTestServiceImpl.getTestResultService() called with dto project: {}, cursor: {}, size: {}", dto.projectKey(), dto.cursor(), dto.size());
    Integer key = dto.projectKey();
    var list = scene.findAllByProjectKey(key);

    return List.of();
  }

  @Override
  public List<TestcaseInfoResponseDto> getTestCaseResultService(
      GetTestCaseResultServiceRequestDto dto) {
    return List.of();
  }
}
