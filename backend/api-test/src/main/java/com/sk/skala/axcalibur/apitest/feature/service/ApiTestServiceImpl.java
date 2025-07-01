package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseSuccessResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepositoryCustom;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTestServiceImpl implements ApiTestService {
  private final TestcaseRepository tc;
  private final TestcaseResultRepository tr;
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

    // TODO: Redis Streams 이용해 비동기 처리 구현하기

    // 시나리오 아이디 리스트와 프로젝트 pk로 시나리오 아이디별 api 목록(매칭표의 단계, pk 추가) 추출
    // API 목록을 기반으로 파라미터 추출
    // 테스트케이스 데이터를 가져와 파라미터와 결합해 API 요청 생성
    // 테스트케이스 ID 기반으로 테스트케이스 결과 칼럼 생성(생성일자은 지금으로 동일하게 고정)
    // redis stream에 작업 목록 추가
    // 작업 목록 추가된 테스트케이스 ID 리스트 반환

    return List.of();
  }

  /**
   * 프로젝트의 모든 시나리오 테스트 결과를 조회합니다. 커서를 기준으로 size만큼의 결과를 반환합니다.
   *
   * @param dto 프로젝트 키, 커서, 페이지 크기 등의 정보를 담은 요청 DTO
   * @return 시나리오별 테스트 결과 리스트
   */
  @Override
  public List<ScenarioResponseDto> getTestResultService(GetTestResultServiceRequestDto dto) {
    log.info(
        "ApiTestServiceImpl.getTestResultService() called with dto project: {}, cursor: {}, size: {}",
        dto.projectKey(), dto.cursor(), dto.size());
    Integer key = dto.projectKey();
    List<ScenarioEntity> scenarios;

    // dto.size is null
    if (dto.size() == null) {
      scenarios = scene.findAllByProjectKey(key);
    } else {
      var cursor = dto.cursor() == null ? "" : dto.cursor();
      var size = dto.size();
      var page = PageRequest.of(0, size, Sort.by("id").ascending());
      scenarios = scene.findAllByProjectKeyAndScenarioIdGreaterThanOrderByIdAsc(key, cursor, page);
    }

    var dtoList = tc.findByScenarioInWithResultSuccess(scenarios);

    // dtoList를 scenarioId 기준으로 그룹핑 (중복 방지, 성능 개선)
    var scenarioMap = scenarios.stream().collect(Collectors.toMap(
        ScenarioEntity::getScenarioId,
        scene -> scene));

    var dtoMap = dtoList.stream().collect(Collectors.groupingBy(TestcaseSuccessResponseDto::scenarioId));
    return dtoMap.entrySet().stream()
        .map(entry -> {
          var scenarioId = entry.getKey();
          var scenario = scenarioMap.get(scenarioId);
          var list = entry.getValue();
          String success;

          if (list.stream().anyMatch(t -> Boolean.FALSE.equals(t.success()))) {
            success = "실패";
          } else if (list.stream().anyMatch(t -> t.success() == null)) {
            success = "실행중";
          } else {
            success = "성공";
          }

          return ScenarioResponseDto.builder()
              .scenarioId(scenarioId)
              .scenarioName(scenario.getName())
              .isSuccess(success)
              .build();
        })
        .toList();
  }

  /**
   * 특정 시나리오의 테스트케이스 결과 목록을 조회합니다.
   *
   * @param dto 프로젝트 키, 시나리오 ID, 커서, 페이지 크기 등의 정보를 담은 요청 DTO
   * @return 테스트케이스별 상세 결과 리스트
   */
  @Override
  public List<TestcaseInfoResponseDto> getTestCaseResultService(
      GetTestCaseResultServiceRequestDto dto) {
    log.info(
        "ApiTestServiceImpl.getTestCaseResultService() called with dto projectKdy: {}, scenarioId: {}, cursor: {}, size: {}",
        dto.projectKey(), dto.scenarioId(), dto.cursor(), dto.size());
    Integer key = dto.projectKey();
    String scenarioId = dto.scenarioId();
    List<TestcaseEntity> testcases;

    // dto.size is null
    if (dto.size() == null) {
      testcases = tc.findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioId(key, scenarioId);
    } else {
      var cursor = dto.cursor() == null ? "" : dto.cursor();
      var size = dto.size();
      var page = PageRequest.of(0, size, Sort.by("id").ascending());
      testcases = tc
          .findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioIdAndTestcaseIdGreaterThanOrderByIdAsc(
              key, scenarioId, cursor, page);
    }
    var testcaseResults = tr.findLastResultByTestcaseIn(testcases);

    var tcMap = testcases.stream()
        .collect(Collectors.toMap(TestcaseEntity::getId, tc -> tc));
    var trMap = testcaseResults.stream()
        .collect(Collectors.toMap(tr -> tr.getTestcase().getId(), tr -> tr));

    return tcMap.entrySet().stream().map(
        entry -> {
          Integer id = entry.getKey();
          var tc = entry.getValue();
          String isSuccess;
          Double time = null;

          if (!trMap.containsKey(id)) {
            isSuccess = "준비중";
          } else if (trMap.get(id).getSuccess() == null) {
            isSuccess = "실행중";
          } else if (trMap.get(id).getSuccess()) {
            isSuccess = "성공";
            // 실행 성공하면 시간도 가져오기
            time = trMap.get(id).getTime();
          } else {
            // 실패한 경우에도 시간은 가져오기
            isSuccess = "실패";
            time = trMap.get(id).getTime();
          }

          return TestcaseInfoResponseDto.builder()
              .tcId(tc.getTestcaseId())
              .description(tc.getDescription())
              .expectedResult(tc.getExpected())
              .isSuccess(isSuccess)
              .excutedTime(time)
              .build();
        }).toList();
  }
}
