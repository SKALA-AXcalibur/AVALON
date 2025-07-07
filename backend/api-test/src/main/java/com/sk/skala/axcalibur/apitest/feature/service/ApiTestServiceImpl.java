package com.sk.skala.axcalibur.apitest.feature.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.skala.axcalibur.apitest.feature.code.StreamConstants;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiRequestDataDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ApiTaskDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestExecutionDataDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ParameterWithDataDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseSuccessResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
import com.sk.skala.axcalibur.apitest.feature.util.ApiTaskDtoConverter;
import com.sk.skala.axcalibur.apitest.global.code.ErrorCode;
import com.sk.skala.axcalibur.apitest.global.exception.BusinessExceptionHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApiTestServiceImpl implements ApiTestService {
  private final TestcaseRepository tc;
  private final TestcaseResultRepository tr;
  private final ScenarioRepository scene;
  private final MappingRepository mr;
  private final ParameterRepository pr;
  private final ApiTestRepository at;
  private final RedisTemplate<String, Object> redis;
  private final ObjectMapper mapper;

  @Override
  public List<String> excuteTestService(ExcuteTestServiceRequestDto dto) {
    log.info("ApiTestServiceImpl.excuteTestService: called with dto size: {}", dto.scenarioList().size());
    var list = dto.scenarioList();
    if (list.isEmpty()) {
      log.warn("ApiTestServiceImpl.excuteTestService: list is empty. No scenarios provided for execution.");
      throw new BusinessExceptionHandler("No scenarios provided for api-test execution",
          ErrorCode.SCENARIO_NOT_FOUND_ERROR);
    }

    List<String> processedTestcaseIdList = new ArrayList<>();

    try {
      // 1. 최적화된 단일 쿼리로 모든 실행 데이터 조회
      List<ApiTestExecutionDataDto> executionDataList = mr
          .findExecutionDataByProjectAndScenarios(dto.projectKey(), list);

      if (executionDataList.isEmpty()) {
        log.warn(
            "ApiTestServiceImpl.excuteTestService: execution data is empty. No data found for project: {} and scenarios: {}",
            dto.projectKey(), list);
        throw new BusinessExceptionHandler("No execution data found for project",
            ErrorCode.INTERNAL_SERVER_ERROR);
      }

      // 2. 테스트케이스 결과 생성을 위한 데이터 준비
      List<TestcaseResultEntity> testcaseResultList = new ArrayList<>();
      List<ApiTestRedisEntity> apiTestRedisEntityList = new ArrayList<>();
      Map<Integer, ApiTestExecutionDataDto> executionDataMap = new HashMap<>();

      // 실제 TestcaseEntity 조회를 위한 캐시
      Map<Integer, TestcaseEntity> testcaseCache = new HashMap<>();

      // 시나리오 별 최대 step 추출
      Map<Integer, Integer> maxStepByScenario = executionDataList.stream()
          .collect(Collectors.toMap(
              ApiTestExecutionDataDto::scenarioId,
              ApiTestExecutionDataDto::step,
              Integer::max // 최대 step 값을 선택
          ));

      // 모든 testcaseId를 한 번에 조회
      List<Integer> allTestcaseIdList = executionDataList.stream()
          .map(ApiTestExecutionDataDto::testcaseId)
          .distinct()
          .toList();

      List<TestcaseEntity> testcases = tc.findAllById(allTestcaseIdList);
      testcases.forEach(testcase -> testcaseCache.put(testcase.getId(), testcase));

      for (ApiTestExecutionDataDto executionData : executionDataList) {
        TestcaseEntity testcase = testcaseCache.get(executionData.testcaseId());
        if (testcase == null) {
          log.warn("TestcaseEntity not found for id: {}", executionData.testcaseId());
          continue;
        }

        TestcaseResultEntity result = TestcaseResultEntity.builder()
            .testcase(testcase) // 실제 엔티티 사용
            .result("") // 초기값 빈 문자열
            .success(null) // 초기값 null (실행중)
            .time(null) // 초기값 null
            .reason(null) // 초기값 null
            .build();
        testcaseResultList.add(result);
        executionDataMap.put(executionData.testcaseId(), executionData);
      }

      for (var entry : maxStepByScenario.entrySet()) {
        Integer scenarioId = entry.getKey();
        Integer maxStep = entry.getValue();
        var entity = ApiTestRedisEntity.builder()
            .id(scenarioId)
            .completed(0) // 초기 완료 단계
            .finish(maxStep) // 최대 단계로 설정
            .build();
        apiTestRedisEntityList.add(entity);
      }

      // 3. 테스트케이스 결과 저장
      testcaseResultList = tr.saveAll(testcaseResultList);
      // 3-1. ApiTestEntity 저장
      at.saveAll(apiTestRedisEntityList);

      // 4. API 목록과 테스트케이스 ID 추출
      List<Integer> apiListIds = executionDataList.stream()
          .map(ApiTestExecutionDataDto::apiListId)
          .distinct()
          .toList();
      List<Integer> testcaseIds = executionDataList.stream()
          .map(ApiTestExecutionDataDto::testcaseId)
          .toList();

      // 5. 단일 쿼리로 파라미터와 테스트케이스 데이터 조회
      List<ParameterWithDataDto> parametersWithData = pr
          .findParametersWithDataByApiListAndTestcase(apiListIds, testcaseIds);

      // 6. 데이터 그룹핑
      Map<Integer, List<ParameterWithDataDto>> parametersByTestcase = parametersWithData.stream()
          .filter(p -> p.testcaseId() != null)
          .collect(Collectors.groupingBy(ParameterWithDataDto::testcaseId));

      // 7. Redis Stream에 작업 목록 추가
      for (int i = 0; i < testcaseResultList.size(); i++) {
        TestcaseResultEntity testcaseResult = testcaseResultList.get(i);
        Integer testcaseId = testcaseResult.getTestcase().getId();
        ApiTestExecutionDataDto executionData = executionDataMap.get(testcaseId);

        if (executionData == null) {
          log.warn("ApiTestServiceImpl.excuteTestService: No execution data found for testcaseId: {}",
              testcaseId);
          continue;
        }

        // 테스트케이스 데이터를 가져와 파라미터와 결합해 API 요청 생성
        List<ParameterWithDataDto> testcaseParameters = parametersByTestcase.getOrDefault(testcaseId,
            new ArrayList<>());
        ApiRequestDataDto requestData = buildTaskData(testcaseParameters);

        MultiValueMap<String, String> reqHeader = requestData.reqHeader();
        MultiValueMap<String, String> reqQuery = requestData.reqQuery();
        Map<String, Object> reqBody = requestData.reqBody();
        Map<String, String> reqPath = requestData.reqPath();

        // 예상 응답 정보 (파라미터에서 추출)
        MultiValueMap<String, String> resHeader = requestData.resHeader();
        Map<String, Object> resBody = requestData.resBody();

        // ApiTaskDto 생성
        ApiTaskDto apiTask = ApiTaskDto.builder()
            .id(executionData.scenarioId())
            .testcaseId(testcaseId)
            .resultId(testcaseResult.getId())
            .precondition(executionData.precondition())
            .step(executionData.step())
            .attempt(1) // 초기 시도 횟수
            .method(executionData.method())
            .uri(executionData.url() + executionData.path())
            .reqHeader(reqHeader)
            .reqBody(reqBody)
            .reqQuery(reqQuery)
            .reqPath(reqPath)
            .statusCode(executionData.status())
            .resHeader(resHeader)
            .resBody(resBody)
            .build();

        // Redis Stream에 메시지 추가
        var dataMap = ApiTaskDtoConverter.toMap(apiTask);
        var record = MapRecord.create(StreamConstants.STREAM_KEY, dataMap);
        var recordId = redis.opsForStream().add(record);

        if (recordId != null) {
          processedTestcaseIdList.add(executionData.testcaseStringId());
          log.debug(
              "ApiTestServiceImpl.excuteTestService: Added task to Redis Stream: testcaseId={}, recordId={}",
              executionData.testcaseStringId(), recordId);
        } else {
          log.error(
              "ApiTestServiceImpl.excuteTestService: Failed to add task to Redis Stream for testcaseId: {}",
              executionData.testcaseStringId());
        }
      }

      log.info("ApiTestServiceImpl.excuteTestService: Successfully processed {} testcases for scenarios: {}",
          processedTestcaseIdList.size(), list);

    } catch (Exception e) {
      log.error("ApiTestServiceImpl.excuteTestService: Error processing test execution for scenarios: {}", list,
          e);
      throw new BusinessExceptionHandler("Error processing test execution", ErrorCode.INTERNAL_SERVER_ERROR, e);
    }

    return processedTestcaseIdList;
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
        "ApiTestServiceImpl.getTestResultService: called with dto project: {}, cursor: {}, size: {}",
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
    // dto.size is null
    if (dto.size() == null || dto.size() <= 0) {
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

          if (list == null || list.isEmpty()) {
            log.warn("ApiTestServiceImpl.getTestResultService: No test results found for scenarioId: {}",
                scenarioId);
            success = "준비중";
          } else if (list.stream().anyMatch(t -> Boolean.FALSE.equals(t.success()))) {
            success = "실패";
          } else if (list.stream().anyMatch(t -> Boolean.TRUE.equals(t.success()))) {
            success = "성공";
          } else {
            success = "실행중";
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
        "ApiTestServiceImpl.getTestCaseResultService: called with dto projectKey: {}, scenarioId: {}, cursor: {}, size: {}",
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
    // dto.size is null
    if (dto.size() == null || dto.size() <= 0) {
      testcases = tc.findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioId(key, scenarioId);
    } else {
      var cursor = dto.cursor() == null ? "" : dto.cursor();
      var size = dto.size();
      var page = PageRequest.of(0, size, Sort.by("id").ascending());
      testcases = tc
          .findByMapping_Scenario_ProjectKeyAndMapping_Scenario_ScenarioIdAndTestcaseIdGreaterThanOrderByIdAsc(
              key, scenarioId, cursor, page);
    }

    return tcMap.entrySet().stream().map(
        entry -> {
          Integer id = entry.getKey();
          var tc = entry.getValue();
          String isSuccess;
          Double time = null;

          if (!trMap.containsKey(id)) {
            isSuccess = "준비중";
          } else if (trMap.get(id).getTime() == null) {
            // time이 null이면 아직 실행중 (초기 상태)
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
              .executedTime(time)
              .build();
        }).toList();
  }

  /**
   * API 요청/응답 데이터를 생성합니다.
   * 조인된 데이터를 사용하여 N+1 문제를 피합니다.
   * 
   * @param parametersWithData 파라미터와 테스트케이스 데이터가 조인된 목록
   * @return API 요청/응답에 필요한 데이터를 담은 DTO
   */
  @Override
  public ApiRequestDataDto buildTaskData(List<ParameterWithDataDto> parametersWithData) {
    log.info("ApiTestServiceImpl.buildTaskData: Building request data with {} parameters",
        parametersWithData.size());

    // 요청 데이터 초기화
    MultiValueMap<String, String> reqHeader = new LinkedMultiValueMap<>();
    MultiValueMap<String, String> reqQuery = new LinkedMultiValueMap<>();
    Map<String, Object> reqBody = new HashMap<>();
    Map<String, String> reqPath = new HashMap<>();

    // 응답 데이터 초기화
    MultiValueMap<String, String> resHeader = new LinkedMultiValueMap<>();
    Map<String, Object> resBody = new HashMap<>();

    // 파라미터 계층 구조를 위한 Map들
    Map<Integer, ParameterWithDataDto> parameterMap = new HashMap<>();
    Map<Integer, List<ParameterWithDataDto>> childrenMap = new HashMap<>();

    // 1. 파라미터 맵 구성 및 부모-자식 관계 정리
    for (ParameterWithDataDto param : parametersWithData) {
      parameterMap.put(param.parameterId(), param);

      if (param.parentId() != null) {
        childrenMap.computeIfAbsent(param.parentId(), k -> new ArrayList<>()).add(param);
      }
    }

    // 2. 루트 파라미터들(parent가 null인 것들)부터 처리
    List<ParameterWithDataDto> rootParameters = parametersWithData.stream()
        .filter(param -> param.parentId() == null)
        .toList();

    for (ParameterWithDataDto parameter : rootParameters) {
      processParameter(parameter, parameterMap, childrenMap, reqHeader, reqQuery, reqBody, reqPath, resHeader,
          resBody, null);
    }

    log.debug(
        "ApiTestServiceImpl.buildTaskData: Built request data - ReqHeader: {}, ReqQuery: {}, ReqBody: {}, ReqPath: {}",
        reqHeader, reqQuery, reqBody, reqPath);
    log.debug("ApiTestServiceImpl.buildTaskData: Built response data - ResHeader: {}, ResBody: {}",
        resHeader,
        resBody);

    return new ApiRequestDataDto(reqHeader, reqQuery, reqBody, reqPath, resHeader, resBody);
  }

  /**
   * 파라미터를 재귀적으로 처리합니다.
   */
  private void processParameter(ParameterWithDataDto parameter,
      Map<Integer, ParameterWithDataDto> parameterMap,
      Map<Integer, List<ParameterWithDataDto>> childrenMap,
      MultiValueMap<String, String> reqHeader, MultiValueMap<String, String> reqQuery,
      Map<String, Object> reqBody, Map<String, String> reqPath,
      MultiValueMap<String, String> resHeader, Map<String, Object> resBody,
      String parentPath) {

    String paramName = parameter.parameterName();
    String fullParamName = parentPath != null ? parentPath + "." + paramName : paramName;
    String categoryName = parameter.categoryName().toLowerCase();
    String contextName = parameter.contextName().toLowerCase();
    String dataType = parameter.dataType() != null ? parameter.dataType().toLowerCase() : "string";

    log.info(
        "ApiTestServiceImpl.processParameter: Processing Parameter: {}, FullPath: {}, Category: {}, Context: {}, DataType: {}",
        paramName, fullParamName, categoryName, contextName, dataType);

    Object processedValue = null;

    if ("array".equals(dataType)) {
      processedValue = processArrayParameter(parameter, parameterMap, childrenMap, parentPath);
    } else if ("object".equals(dataType)) {
      processedValue = processObjectParameter(parameter, parameterMap, childrenMap, parentPath);
    } else if (parameter.value() != null) {
      processedValue = convertValueByDataType(parameter.value(), dataType);
    }

    // 값이 있는 경우에만 적절한 위치에 배치
    if (processedValue != null) {
      String targetParamName = parentPath != null ? fullParamName : paramName;

      if ("request".equals(categoryName)) {
        placeRequestValue(contextName, targetParamName, processedValue, reqHeader, reqQuery, reqBody, reqPath);
      } else if ("response".equals(categoryName)) {
        placeResponseValue(contextName, targetParamName, processedValue, resHeader, resBody);
      } else {
        // category가 명시되지 않은 경우 request로 처리
        placeRequestValue(contextName, targetParamName, processedValue, reqHeader, reqQuery, reqBody, reqPath);
      }
    } else {
      log.debug("ApiTestServiceImpl.processParameter: No test data found for parameter: {}",
          fullParamName);
    }
  }

  /**
   * Array 타입 파라미터를 처리합니다.
   */
  private Object processArrayParameter(ParameterWithDataDto parameter,
      Map<Integer, ParameterWithDataDto> parameterMap,
      Map<Integer, List<ParameterWithDataDto>> childrenMap,
      String parentPath) {
    log.info("ApiTestServiceImpl.processArrayParameter: Processing array parameter: {}",
        parameter.parameterName());

    List<Object> arrayList = new ArrayList<>();
    List<ParameterWithDataDto> children = childrenMap.get(parameter.parameterId());

    if (children != null && !children.isEmpty()) {
      for (ParameterWithDataDto child : children) {
        if ("object".equals(child.dataType())) {
          Object childObject = processObjectParameter(child, parameterMap, childrenMap, parentPath);
          if (childObject instanceof Map && !((Map<?, ?>) childObject).isEmpty()) {
            arrayList.add(childObject);
          }
        } else {
          if (child.value() != null) {
            Object childValue = convertValueByDataType(child.value(), child.dataType());
            arrayList.add(childValue);
          }
        }
      }
    } else {
      // 자식이 없는 경우 직접 값을 배열로 처리
      if (parameter.value() != null) {
        String[] values = parameter.value().split(",");
        for (String value : values) {
          arrayList.add(value.trim());
        }
      }
    }

    return arrayList.isEmpty() ? null : arrayList;
  }

  /**
   * Object 타입 파라미터를 처리합니다.
   */
  private Object processObjectParameter(ParameterWithDataDto parameter,
      Map<Integer, ParameterWithDataDto> parameterMap,
      Map<Integer, List<ParameterWithDataDto>> childrenMap,
      String parentPath) {
    log.info("ApiTestServiceImpl.processObjectParameter: Processing object parameter: {}",
        parameter.parameterName());

    Map<String, Object> objectMap = new HashMap<>();
    String currentPath = parentPath != null ? parentPath + "." + parameter.parameterName()
        : parameter.parameterName();
    List<ParameterWithDataDto> children = childrenMap.get(parameter.parameterId());

    if (children != null && !children.isEmpty()) {
      for (ParameterWithDataDto child : children) {
        String childDataType = child.dataType() != null ? child.dataType().toLowerCase() : "string";

        if ("array".equals(childDataType)) {
          Object arrayValue = processArrayParameter(child, parameterMap, childrenMap, currentPath);
          if (arrayValue != null) {
            objectMap.put(child.parameterName(), arrayValue);
          }
        } else if ("object".equals(childDataType)) {
          Object childObject = processObjectParameter(child, parameterMap, childrenMap, currentPath);
          if (childObject != null) {
            objectMap.put(child.parameterName(), childObject);
          }
        } else {
          if (child.value() != null) {
            Object childValue = convertValueByDataType(child.value(), childDataType);
            objectMap.put(child.parameterName(), childValue);
          }
        }
      }
    } else {
      // 자식이 없는 경우 JSON 문자열로 파싱 시도
      if (parameter.value() != null) {
        try {
          objectMap = mapper.readValue(parameter.value(), new TypeReference<Map<String, Object>>() {
          });
        } catch (IOException e) {
          log.warn(
              "ApiTestServiceImpl.processObjectParameter: Failed to parse JSON for parameter: {}, value: {}",
              parameter.parameterName(), parameter.value());
          objectMap.put(parameter.parameterName(), parameter.value());
        }
      }
    }

    if (objectMap == null || objectMap.isEmpty()) {
      return null; // 빈 객체는 null로 처리
    } else
      return objectMap;
  }

  /**
   * 요청 데이터에 값을 배치합니다.
   */
  private void placeRequestValue(String contextName, String paramName, Object value,
      MultiValueMap<String, String> reqHeader, MultiValueMap<String, String> reqQuery,
      Map<String, Object> reqBody, Map<String, String> reqPath) {
    log.info("ApiTestServiceImpl.placeRequestValue: Placing value for context: {}, paramName: {}, value: {}",
        contextName, paramName, value);
    switch (contextName) {
      case "header":
        reqHeader.add(paramName, value.toString());
        break;
      case "query":
        reqQuery.add(paramName, value.toString());
        break;
      case "body":
        reqBody.put(paramName, value);
        break;
      case "path":
        reqPath.put(paramName, value.toString());
        break;
      case "session":
      case "cookie":
        reqHeader.add("Cookie", paramName + "=" + value.toString());
        break;
      default:
        log.warn("ApiTestServiceImpl.placeRequestValue: Unknown request context: {} for parameter: {}",
            contextName, paramName);
        break;
    }
  }

  /**
   * 응답 데이터에 값을 배치합니다.
   */
  private void placeResponseValue(String contextName, String paramName, Object value,
      MultiValueMap<String, String> resHeader, Map<String, Object> resBody) {
    log.info("ApiTestServiceImpl.placeResponseValue: Placing value for context: {}, paramName: {}, value: {}",
        contextName, paramName, value);
    // 응답 데이터는 header와 body만 처리
    switch (contextName) {
      case "header":
        resHeader.add(paramName, value.toString());
        break;
      case "body":
        resBody.put(paramName, value);
        break;
      case "query":
      case "path":
        log.debug("ApiTestServiceImpl.placeResponseValue: Ignoring response {} parameter: {}", contextName,
            paramName);
        break;
      default:
        log.warn("ApiTestServiceImpl.placeResponseValue: Unknown response context: {} for parameter: {}",
            contextName, paramName);
        break;
    }
  }

  /**
   * 데이터 타입에 따라 문자열 값을 적절한 타입으로 변환합니다.
   * 
   * @param value    변환할 문자열 값
   * @param dataType 목표 데이터 타입
   * @return 변환된 값
   */
  @Override
  public Object convertValueByDataType(String value, String dataType) {
    log.info("ApiTestServiceImpl.convertValueByDataType: Converting value '{}' to type '{}'", value, dataType);
    if (value == null || value.trim().isEmpty()) {
      return value;
    }

    try {
      switch (dataType.toLowerCase()) {
        case "bigdecimal":
          return new BigDecimal(value);
        case "integer":
        case "int":
          return Integer.parseInt(value);
        case "long":
          return Long.parseLong(value);
        case "double":
          return Double.parseDouble(value);
        case "float":
          return Float.parseFloat(value);
        case "boolean":
          return Boolean.parseBoolean(value);
        case "array":
          // 배열은 쉼표로 구분된 문자열로 가정
          return value.split(",");
        case "object":
        case "string":
        default:
          return value;
      }
    } catch (NumberFormatException e) {
      log.warn(
          "ApiTestServiceImpl.convertValueByDataType: Failed to convert value '{}' to type '{}', using as string",
          value, dataType);
      return value;
    }
  }
}
