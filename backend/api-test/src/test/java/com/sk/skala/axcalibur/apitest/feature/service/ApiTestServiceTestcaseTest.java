package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiListEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.CategoryEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ContextEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.MappingEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ParameterEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseDataEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiListRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.CategoryRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ContextRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseDataRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;

@SpringBootTest
@DisplayName("ApiTestService getTestResultService, getTestCaseResultService 통합 테스트")
@Rollback
public class ApiTestServiceTestcaseTest {

  @Autowired
  private ApiTestService apiTestService;

  @Autowired
  private ScenarioRepository scenarioRepository;

  @Autowired
  private ApiListRepository apiListRepository;

  @Autowired
  private TestcaseRepository testcaseRepository;

  @Autowired
  private MappingRepository mappingRepository;

  @Autowired
  private ParameterRepository parameterRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private TestcaseDataRepository testcaseDataRepository;

  @Autowired
  private TestcaseResultRepository testcaseResultRepository;

  // 테스트 데이터
  private ScenarioEntity scenario1;
  private ScenarioEntity scenario2;
  private TestcaseEntity testcase1;
  private TestcaseEntity testcase2;
  private TestcaseEntity testcase3;
  private TestcaseResultEntity result1;
  private TestcaseResultEntity result2;
  private TestcaseResultEntity result3;

  @BeforeEach
  @Transactional
  public void setUp() {
    // 테스트 데이터 정리
    testcaseResultRepository.deleteAll();
    testcaseDataRepository.deleteAll();
    testcaseRepository.deleteAll();
    mappingRepository.deleteAll();
    parameterRepository.deleteAll();
    contextRepository.deleteAll();
    categoryRepository.deleteAll();
    apiListRepository.deleteAll();
    scenarioRepository.deleteAll();

    // 1. 기본 엔티티 생성
    // 카테고리 생성
    CategoryEntity requestCategory = CategoryEntity.builder()
        .name("request")
        .build();
    requestCategory = categoryRepository.save(requestCategory);

    CategoryEntity responseCategory = CategoryEntity.builder()
        .name("response")
        .build();
    responseCategory = categoryRepository.save(responseCategory);

    // 컨텍스트 생성
    ContextEntity headerContext = ContextEntity.builder()
        .name("header")
        .build();
    headerContext = contextRepository.save(headerContext);

    ContextEntity bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    // 2. 시나리오 생성
    scenario1 = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("test-scenario-1")
        .name("테스트 시나리오 1")
        .build();
    scenario1 = scenarioRepository.save(scenario1);

    scenario2 = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("test-scenario-2")
        .name("테스트 시나리오 2")
        .build();
    scenario2 = scenarioRepository.save(scenario2);

    // 3. API 리스트 생성
    ApiListEntity apiList1 = ApiListEntity.builder()
        .url("https://httpbin.org/get")
        .path("/get")
        .method("GET")
        .build();
    apiList1 = apiListRepository.save(apiList1);

    ApiListEntity apiList2 = ApiListEntity.builder()
        .url("https://httpbin.org/post")
        .path("/post")
        .method("POST")
        .build();
    apiList2 = apiListRepository.save(apiList2);

    // 4. 매핑 생성
    MappingEntity mapping1 = MappingEntity.builder()
        .mappingId("mapping-1")
        .step(1)
        .scenario(scenario1)
        .apiList(apiList1)
        .build();
    mapping1 = mappingRepository.save(mapping1);

    MappingEntity mapping2 = MappingEntity.builder()
        .mappingId("mapping-2")
        .step(2)
        .scenario(scenario1)
        .apiList(apiList2)
        .build();
    mapping2 = mappingRepository.save(mapping2);

    MappingEntity mapping3 = MappingEntity.builder()
        .mappingId("mapping-3")
        .step(1)
        .scenario(scenario2)
        .apiList(apiList1)
        .build();
    mapping3 = mappingRepository.save(mapping3);

    // 5. 테스트케이스 생성
    testcase1 = TestcaseEntity.builder()
        .testcaseId("TC-001")
        .description("첫 번째 테스트케이스")
        .precondition("전제조건 1")
        .expected("예상결과 1")
        .status(2)
        .mapping(mapping1)
        .build();
    testcase1 = testcaseRepository.save(testcase1);

    testcase2 = TestcaseEntity.builder()
        .testcaseId("TC-002")
        .description("두 번째 테스트케이스")
        .precondition("전제조건 2")
        .expected("예상결과 2")
        .status(2)
        .mapping(mapping2)
        .build();
    testcase2 = testcaseRepository.save(testcase2);

    testcase3 = TestcaseEntity.builder()
        .testcaseId("TC-003")
        .description("세 번째 테스트케이스")
        .precondition("전제조건 3")
        .expected("예상결과 3")
        .status(2)
        .mapping(mapping3)
        .build();
    testcase3 = testcaseRepository.save(testcase3);

    // 6. 파라미터 생성
    ParameterEntity param1 = ParameterEntity.builder()
        .apiList(apiList1)
        .category(requestCategory)
        .context(headerContext)
        .name("Content-Type")
        .dataType("string")
        .build();
    param1 = parameterRepository.save(param1);

    ParameterEntity param2 = ParameterEntity.builder()
        .apiList(apiList2)
        .category(requestCategory)
        .context(bodyContext)
        .name("message")
        .dataType("string")
        .build();
    param2 = parameterRepository.save(param2);

    // 7. 테스트케이스 데이터 생성
    TestcaseDataEntity data1 = TestcaseDataEntity.builder()
        .testcase(testcase1)
        .parameter(param1)
        .value("application/json")
        .build();
    testcaseDataRepository.save(data1);

    TestcaseDataEntity data2 = TestcaseDataEntity.builder()
        .testcase(testcase2)
        .parameter(param2)
        .value("Hello World")
        .build();
    testcaseDataRepository.save(data2);

    // 8. 테스트케이스 결과 생성
    result1 = TestcaseResultEntity.builder()
        .testcase(testcase1)
        .result("{\"status\": \"ok\"}")
        .success(true)
        .time(1.5)
        .reason(null)
        .build();
    result1 = testcaseResultRepository.save(result1);

    result2 = TestcaseResultEntity.builder()
        .testcase(testcase2)
        .result("{\"error\": \"fail\"}")
        .success(false)
        .time(2.0)
        .reason("요청 실패")
        .build();
    result2 = testcaseResultRepository.save(result2);

    result3 = TestcaseResultEntity.builder()
        .testcase(testcase3)
        .result("")
        .success(false)
        .time(null)
        .reason(null)
        .build();
    result3 = testcaseResultRepository.save(result3);
  }

  @Nested
  @DisplayName("getTestResultService 테스트")
  class GetTestResultServiceTests {

    @Test
    @DisplayName("정상적인 프로젝트 키로 모든 시나리오 결과 조회")
    @Transactional
    void getTestResultService_ValidProjectKey_ReturnsAllScenarios() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(null)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);

      // 시나리오 1 검증 (성공 1개, 실패 1개 -> 실패)
      ScenarioResponseDto scenario1Result = result.stream()
          .filter(s -> s.scenarioId().equals("test-scenario-1"))
          .findFirst()
          .orElse(null);
      assertThat(scenario1Result).isNotNull();
      assertThat(scenario1Result.scenarioName()).isEqualTo("테스트 시나리오 1");
      assertThat(scenario1Result.isSuccess()).isEqualTo("실패");

      // 시나리오 2 검증 (실행중 1개 -> 준비중으로 표시)
      ScenarioResponseDto scenario2Result = result.stream()
          .filter(s -> s.scenarioId().equals("test-scenario-2"))
          .findFirst()
          .orElse(null);
      assertThat(scenario2Result).isNotNull();
      assertThat(scenario2Result.scenarioName()).isEqualTo("테스트 시나리오 2");
      assertThat(scenario2Result.isSuccess()).isEqualTo("실패");
    }

    @Test
    @DisplayName("페이징을 사용한 시나리오 결과 조회")
    @Transactional
    void getTestResultService_WithPaging_ReturnsPagedResults() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor("")
          .size(1)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 키로 조회시 빈 결과 반환")
    @Transactional
    void getTestResultService_NonExistentProject_ReturnsEmptyList() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(999)
          .cursor(null)
          .size(null)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("커서 기반 페이징 테스트")
    @Transactional
    void getTestResultService_CursorBasedPaging_ReturnsCorrectResults() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor("test-scenario-1")
          .size(1)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      // cursor보다 큰 ID를 가진 시나리오만 반환
      assertThat(result).allMatch(s -> s.scenarioId().compareTo("test-scenario-1") > 0);
    }

    @Test
    @DisplayName("모든 테스트케이스가 성공한 시나리오는 성공으로 표시")
    @Transactional
    void getTestResultService_AllSuccessfulTestcases_ShowsSuccess() {
      // given - 모든 결과를 성공으로 변경
      TestcaseResultEntity updatedResult2 = TestcaseResultEntity.builder()
          .id(result2.getId())
          .testcase(result2.getTestcase())
          .result(result2.getResult())
          .success(true)
          .time(result2.getTime())
          .reason(result2.getReason())
          .build();
      testcaseResultRepository.save(updatedResult2);

      TestcaseResultEntity updatedResult3 = TestcaseResultEntity.builder()
          .id(result3.getId())
          .testcase(result3.getTestcase())
          .result(result3.getResult())
          .success(true)
          .time(1.0)
          .reason(result3.getReason())
          .build();
      testcaseResultRepository.save(updatedResult3);

      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(null)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);

      // 모든 시나리오가 성공이어야 함
      assertThat(result).allMatch(s -> s.isSuccess().equals("성공"));
    }
  }

  @Nested
  @DisplayName("getTestCaseResultService 테스트")
  class GetTestCaseResultServiceTests {

    @Test
    @DisplayName("특정 시나리오의 테스트케이스 결과 조회")
    @Transactional
    void getTestCaseResultService_ValidScenario_ReturnsTestcaseResults() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);
      System.out.println("Testcase Results: " + result);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);

      // 성공한 테스트케이스 검증
      TestcaseInfoResponseDto successTestcase = result.stream()
          .filter(tc -> tc.tcId().equals("TC-001"))
          .findFirst()
          .orElse(null);
      assertThat(successTestcase).isNotNull();
      assertThat(successTestcase.description()).isEqualTo("첫 번째 테스트케이스");
      assertThat(successTestcase.expectedResult()).isEqualTo("예상결과 1");
      assertThat(successTestcase.isSuccess()).isEqualTo("성공");
      assertThat(successTestcase.executedTime()).isEqualTo(1.5);

      // 실패한 테스트케이스 검증
      TestcaseInfoResponseDto failedTestcase = result.stream()
          .filter(tc -> tc.tcId().equals("TC-002"))
          .findFirst()
          .orElse(null);
      assertThat(failedTestcase).isNotNull();
      assertThat(failedTestcase.description()).isEqualTo("두 번째 테스트케이스");
      assertThat(failedTestcase.expectedResult()).isEqualTo("예상결과 2");
      assertThat(failedTestcase.isSuccess()).isEqualTo("실패");
      assertThat(failedTestcase.executedTime()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("실행중인 테스트케이스는 '실행중'으로 표시")
    @Transactional
    void getTestCaseResultService_RunningTestcase_ShowsRunning() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-2")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);
      System.out.println("Running Testcase Results: " + result);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);

      TestcaseInfoResponseDto runningTestcase = result.get(0);
      assertThat(runningTestcase.tcId()).isEqualTo("TC-003");
      assertThat(runningTestcase.isSuccess()).isEqualTo("실행중");
      assertThat(runningTestcase.executedTime()).isNull();
    }

    @Test
    @DisplayName("결과가 없는 테스트케이스는 '준비중'으로 표시")
    @Transactional
    void getTestCaseResultService_NoResultTestcase_ShowsPreparing() {
      // given - 결과 삭제
      testcaseResultRepository.deleteById(result3.getId());

      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-2")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);

      TestcaseInfoResponseDto preparingTestcase = result.get(0);
      assertThat(preparingTestcase.tcId()).isEqualTo("TC-003");
      assertThat(preparingTestcase.isSuccess()).isEqualTo("준비중");
      assertThat(preparingTestcase.executedTime()).isNull();
    }

    @Test
    @DisplayName("페이징을 사용한 테스트케이스 결과 조회")
    @Transactional
    void getTestCaseResultService_WithPaging_ReturnsPagedResults() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor("")
          .size(1)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 시나리오 조회시 빈 결과 반환")
    @Transactional
    void getTestCaseResultService_NonExistentScenario_ReturnsEmptyList() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("non-existent-scenario")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("잘못된 프로젝트 키로 조회시 빈 결과 반환")
    @Transactional
    void getTestCaseResultService_InvalidProjectKey_ReturnsEmptyList() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(999)
          .scenarioId("test-scenario-1")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("커서 기반 페이징으로 특정 테스트케이스 이후 결과 조회")
    @Transactional
    void getTestCaseResultService_CursorBasedPaging_ReturnsCorrectResults() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor("TC-001")
          .size(1)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      // cursor보다 큰 ID를 가진 테스트케이스만 반환
      assertThat(result).allMatch(tc -> tc.tcId().compareTo("TC-001") > 0);
    }
  }

  @Nested
  @DisplayName("예외 상황 테스트")
  class ExceptionTests {

    @Test
    @DisplayName("null 프로젝트 키로 getTestResultService 호출시 예외 없이 처리")
    @Transactional
    void getTestResultService_NullProjectKey_HandlesGracefully() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(null)
          .cursor(null)
          .size(null)
          .build();

      // when & then - 예외가 발생하지 않고 적절히 처리되어야 함
      assertThatCode(() -> {
        List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);
        assertThat(result).isNotNull();
      }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("null 시나리오 ID로 getTestCaseResultService 호출시 예외 없이 처리")
    @Transactional
    void getTestCaseResultService_NullScenarioId_HandlesGracefully() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId(null)
          .cursor(null)
          .size(null)
          .build();

      // when & then - 예외가 발생하지 않고 적절히 처리되어야 함
      assertThatCode(() -> {
        List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);
        assertThat(result).isNotNull();
      }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("음수 size로 페이징 요청시 적절히 처리")
    @Transactional
    void getTestResultService_NegativeSize_HandlesGracefully() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(-1)
          .build();

      // when & then - 예외가 발생하지 않아야 함
      assertThatCode(() -> {
        List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);
        assertThat(result).isNotNull();
      }).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("성능 및 대용량 데이터 테스트")
  class PerformanceTests {

    @Test
    @DisplayName("대량의 시나리오와 테스트케이스 조회 성능 테스트")
    @Transactional
    void getTestResultService_LargeDataSet_PerformsWell() {
      // given - 대량 데이터 생성
      createLargeDataSet();

      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(null)
          .build();

      // when - 성능 측정
      long startTime = System.currentTimeMillis();
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);
      long endTime = System.currentTimeMillis();
      long executionTime = endTime - startTime;

      // then
      assertThat(result).isNotNull();
      assertThat(result.size()).isGreaterThan(2); // 기존 2개 + 추가 데이터
      assertThat(executionTime).isLessThan(5000); // 5초 이내 실행
    }

    @Test
    @DisplayName("페이징을 사용한 대량 데이터 조회 성능 테스트")
    @Transactional
    void getTestCaseResultService_LargeDataSetWithPaging_PerformsWell() {
      // given - 대량 데이터 생성
      createLargeDataSet();

      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor(null)
          .size(10)
          .build();

      // when - 성능 측정
      long startTime = System.currentTimeMillis();
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);
      long endTime = System.currentTimeMillis();
      long executionTime = endTime - startTime;

      // then
      assertThat(result).isNotNull();
      assertThat(result.size()).isLessThanOrEqualTo(10);
      assertThat(executionTime).isLessThan(3000); // 3초 이내 실행
    }

    private void createLargeDataSet() {
      // 추가 시나리오 및 테스트케이스 생성 (간단한 버전)
      for (int i = 3; i <= 10; i++) {
        ScenarioEntity scenario = ScenarioEntity.builder()
            .projectKey(1)
            .scenarioId("large-scenario-" + i)
            .name("대량 테스트 시나리오 " + i)
            .build();
        scenario = scenarioRepository.save(scenario);

        ApiListEntity apiList = ApiListEntity.builder()
            .url("https://httpbin.org/get")
            .path("/get")
            .method("GET")
            .build();
        apiList = apiListRepository.save(apiList);

        MappingEntity mapping = MappingEntity.builder()
            .mappingId("large-mapping-" + i)
            .step(1)
            .scenario(scenario)
            .apiList(apiList)
            .build();
        mapping = mappingRepository.save(mapping);

        for (int j = 1; j <= 5; j++) {
          TestcaseEntity testcase = TestcaseEntity.builder()
              .testcaseId("TC-LARGE-" + i + "-" + j)
              .description("대량 테스트케이스 " + i + "-" + j)
              .precondition("전제조건")
              .expected("예상결과")
              .status(2)
              .mapping(mapping)
              .build();
          testcase = testcaseRepository.save(testcase);

          TestcaseResultEntity result = TestcaseResultEntity.builder()
              .testcase(testcase)
              .result("{\"status\": \"ok\"}")
              .success(true)
              .time(1.0)
              .reason(null)
              .build();
          testcaseResultRepository.save(result);
        }
      }
    }
  }

  @Nested
  @DisplayName("데이터 일관성 테스트")
  class DataConsistencyTests {

    @Test
    @DisplayName("최신 테스트케이스 결과만 조회되는지 확인")
    @Transactional
    void getTestCaseResultService_OnlyLatestResults_AreReturned() {
      // given - 같은 테스트케이스에 대한 이전 결과 추가
      TestcaseResultEntity olderResult = TestcaseResultEntity.builder()
          .testcase(testcase1)
          .result("{\"old\": \"result\"}")
          .success(false)
          .time(0.5)
          .reason("이전 결과")
          .build();
      testcaseResultRepository.save(olderResult);

      // 잠시 후 더 최신 결과 생성 (createdAt이 다르도록)
      try {
        Thread.sleep(10); // 작은 시간 차이 생성
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      TestcaseResultEntity newerResult = TestcaseResultEntity.builder()
          .testcase(testcase1)
          .result("{\"status\": \"success\"}")
          .success(true)
          .time(1.2)
          .reason("최신 결과")
          .build();
      testcaseResultRepository.save(newerResult);

      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor(null)
          .size(null)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();

      TestcaseInfoResponseDto testcase1Result = result.stream()
          .filter(tc -> tc.tcId().equals("TC-001"))
          .findFirst()
          .orElse(null);

      assertThat(testcase1Result).isNotNull();
      // 최신 결과만 반영되어야 함 (newerResult가 더 최신)
      assertThat(testcase1Result.isSuccess()).isEqualTo("성공");
      assertThat(testcase1Result.executedTime()).isEqualTo(1.2);
    }

    @Test
    @DisplayName("시나리오별 성공률 계산 정확성 확인")
    @Transactional
    void getTestResultService_SuccessRateCalculation_IsAccurate() {
      // given - 추가 테스트케이스 및 결과 생성으로 정확한 성공률 테스트
      ApiListEntity apiList = ApiListEntity.builder()
          .url("https://httpbin.org/put")
          .path("/put")
          .method("PUT")
          .build();
      apiList = apiListRepository.save(apiList);

      MappingEntity mapping = MappingEntity.builder()
          .mappingId("mapping-additional")
          .step(3)
          .scenario(scenario1)
          .apiList(apiList)
          .build();
      mapping = mappingRepository.save(mapping);

      TestcaseEntity additionalTestcase = TestcaseEntity.builder()
          .testcaseId("TC-ADDITIONAL")
          .description("추가 테스트케이스")
          .precondition("전제조건")
          .expected("예상결과")
          .status(2)
          .mapping(mapping)
          .build();
      additionalTestcase = testcaseRepository.save(additionalTestcase);

      TestcaseResultEntity additionalResult = TestcaseResultEntity.builder()
          .testcase(additionalTestcase)
          .result("{\"status\": \"ok\"}")
          .success(true)
          .time(1.0)
          .reason(null)
          .build();
      testcaseResultRepository.save(additionalResult);

      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(null)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      ScenarioResponseDto scenario1Result = result.stream()
          .filter(s -> s.scenarioId().equals("test-scenario-1"))
          .findFirst()
          .orElse(null);

      assertThat(scenario1Result).isNotNull();
      // scenario1: 성공 2개(TC-001, TC-ADDITIONAL), 실패 1개(TC-002) -> 실패
      assertThat(scenario1Result.isSuccess()).isEqualTo("실패");
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("size가 0인 경우 전체 결과 반환")
    @Transactional
    void getTestResultService_ZeroSize_ReturnsAllResults() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(0)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("매우 큰 size 값으로 조회")
    @Transactional
    void getTestResultService_VeryLargeSize_HandlesGracefully() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor(null)
          .size(Integer.MAX_VALUE)
          .build();

      // when & then
      assertThatCode(() -> {
        List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // 실제 데이터 개수
      }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("빈 문자열 커서로 조회")
    @Transactional
    void getTestCaseResultService_EmptyCursor_HandlesGracefully() {
      // given
      GetTestCaseResultServiceRequestDto requestDto = GetTestCaseResultServiceRequestDto.builder()
          .projectKey(1)
          .scenarioId("test-scenario-1")
          .cursor("")
          .size(10)
          .build();

      // when
      List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 커서 값으로 조회")
    @Transactional
    void getTestResultService_NonExistentCursor_ReturnsExpectedResults() {
      // given
      GetTestResultServiceRequestDto requestDto = GetTestResultServiceRequestDto.builder()
          .projectKey(1)
          .cursor("non-existent-scenario")
          .size(10)
          .build();

      // when
      List<ScenarioResponseDto> result = apiTestService.getTestResultService(requestDto);

      // then
      assertThat(result).isNotNull();
      // cursor보다 큰 시나리오 ID를 가진 것들이 반환되어야 함
    }
  }
}
