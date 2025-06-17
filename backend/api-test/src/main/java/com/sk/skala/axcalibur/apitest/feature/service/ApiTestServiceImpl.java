package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseSuccessResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
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

  private final TestcaseResultRepository repo;
  private final TestcaseRepository tc;
  private final TestcaseRepositoryCustom tcCustom;
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
    var cursor = dto.cursor() == null ? "" : dto.cursor();
    var size = dto.size() == null ? Integer.MAX_VALUE : dto.size();

    var page = PageRequest.of(0, size, Sort.by("id").ascending());
    var scenarios = scene.findAllByProjectKeyAndScenarioIdGreaterThanOrderByIdAsc(key, cursor, page);
    var dtoList = tcCustom.findByScenarioInWithResultSuccess(scenarios);

    // dtoList를 scenarioId 기준으로 그룹핑 (중복 방지, 성능 개선)
    var scenarioMap = scenarios.stream().collect(Collectors.toMap(
        ScenarioEntity::getScenarioId,
        scene -> scene
    ));

    var dtoMap = dtoList.stream().collect(Collectors.groupingBy(TestcaseSuccessResponseDto::scenarioId));
    return dtoMap.entrySet().stream()
        .map(entry -> {
          var scenarioId = entry.getKey();
          var scenario = scenarioMap.get(scenarioId);
          var list = entry.getValue();
          String success;

          if(list.stream().anyMatch(t -> Boolean.FALSE.equals(t.success()))) {
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
        "ApiTestServiceImpl.getTestCaseResultService() called with dto scenarioId: {}, cursor: {}, size: {}",
        dto.scenarioId(), dto.cursor(), dto.size());
    // TODO: 시나리오 ID로 testcase 엔티티 리스트 조회, 커서, 사이즈 고려




    return List.of();
  }
}
