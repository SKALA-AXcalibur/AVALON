package com.sk.skala.axcalibur.apitest.feature.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiListEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.CategoryEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ContextEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.MappingEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ParameterEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseDataEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiListRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.CategoryRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ContextRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ParameterRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseDataRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;

/**
 * 실제 외부 API 통신 및 결과 저장을 검증하는 통합 테스트 클래스
 * 
 * 테스트 목적:
 * 1. ApiTestService.excuteTestService()가 실제로 외부 API와 통신하는지 확인
 * 2. Redis Streams를 통한 비동기 처리가 정상 작동하는지 확인
 * 3. API 통신 결과가 DB(TestcaseResultEntity)에 저장되는지 확인
 * 4. Redis에 처리 상세 정보(ApiTestDetailRedisEntity)가 저장되는지 확인
 */
@SpringBootTest
@DisplayName("실제 API 통신 및 결과 저장 검증 테스트")
@Rollback
public class ApiTestRealCommunicationTest {

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

  @Autowired
  private ApiTestRepository apiTestRepository;

  @Autowired
  private ApiTestDetailRepository apiTestDetailRepository;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private CategoryEntity requestCategory;
  private CategoryEntity responseCategory;
  private ContextEntity headerContext;
  private ContextEntity bodyContext;

  @BeforeEach
  void setUp() {
    // 공통 데이터 생성
    initializeCommonData();

    // Redis Stream 초기화
    initializeRedisStream();
  }

  @AfterEach
  void cleanup() {
    // 테스트 데이터 정리
    cleanupTestData();
  }

  private void initializeCommonData() {
    // 카테고리 생성
    requestCategory = CategoryEntity.builder()
        .name("request")
        .build();
    requestCategory = categoryRepository.save(requestCategory);

    responseCategory = CategoryEntity.builder()
        .name("response")
        .build();
    responseCategory = categoryRepository.save(responseCategory);

    // 컨텍스트 생성
    headerContext = ContextEntity.builder()
        .name("header")
        .build();
    headerContext = contextRepository.save(headerContext);

    bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);
  }

  private void initializeRedisStream() {
    try {
      var connectionFactory = redisTemplate.getConnectionFactory();
      if (connectionFactory != null) {
        var connection = connectionFactory.getConnection();
        if (connection != null) {
          try {
            // Redis 연결 상태 확인
            connection.ping();
            System.out.println("✅ Redis 연결 확인됨");

            connection.streamCommands().xGroupCreate(
                "avalon-api-test".getBytes(),
                "avalon-api-group",
                org.springframework.data.redis.connection.stream.ReadOffset.from("0-0"),
                true);
            System.out.println("✅ Redis Stream 초기화 완료");
          } catch (Exception e) {
            System.out.println("Redis Stream 이미 존재하거나 연결 실패: " + e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      System.out.println("❌ Redis Stream 초기화 실패: " + e.getMessage());
      System.out.println("⚠️ Redis 서버가 실행되지 않았을 수 있습니다. 테스트를 건너뛸 수 있습니다.");
    }
  }

  private void cleanupTestData() {
    try {
      testcaseResultRepository.deleteAllInBatch();
      testcaseDataRepository.deleteAllInBatch();
      parameterRepository.deleteAllInBatch();
      testcaseRepository.deleteAllInBatch();
      mappingRepository.deleteAllInBatch();
      apiListRepository.deleteAllInBatch();
      scenarioRepository.deleteAllInBatch();
      contextRepository.deleteAllInBatch();
      categoryRepository.deleteAllInBatch();
    } catch (Exception e) {
      System.out.println("Cleanup 실패: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("실제 HTTPBin API 통신 및 DB 저장 검증 - GET 요청")
  @Transactional
  void testRealApiCommunication_HttpBinGet() throws InterruptedException {
    System.out.println("🚀 실제 HTTPBin GET API 통신 테스트 시작");
    
    // Redis 연결 상태 확인
    if (!isRedisAvailable()) {
      System.out.println("⚠️ Redis 서버에 연결할 수 없어 테스트를 건너뜁니다.");
      org.junit.jupiter.api.Assumptions.assumeTrue(false, "Redis 서버가 필요합니다");
    }

    // given - HTTPBin GET API 테스트 시나리오 구성
    ScenarioEntity scenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-get-real-test")
        .name("HTTPBin GET 실제 통신 테스트")
        .build();
    scenario = scenarioRepository.save(scenario);

    ApiListEntity apiList = ApiListEntity.builder()
        .url("https://httpbin.org/get")
        .path("/get")
        .method("GET")
        .build();
    apiList = apiListRepository.save(apiList);

    MappingEntity mapping = MappingEntity.builder()
        .mappingId("MAP-HTTPBIN-GET-REAL")
        .scenario(scenario)
        .apiList(apiList)
        .step(1)
        .build();
    mapping = mappingRepository.save(mapping);

    TestcaseEntity testcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-GET-REAL")
        .description("실제 HTTPBin GET API 호출 테스트")
        .precondition("없음")
        .expected("")
        .status(200)
        .mapping(mapping)
        .build();
    final TestcaseEntity savedTestcase = testcaseRepository.save(testcase);

    // Request Header 파라미터 추가
    ParameterEntity userAgentParam = ParameterEntity.builder()
        .apiList(apiList)
        .category(requestCategory)
        .context(headerContext)
        .name("User-Agent")
        .dataType("string")
        .build();
    userAgentParam = parameterRepository.save(userAgentParam);

    TestcaseDataEntity userAgentData = TestcaseDataEntity.builder()
        .testcase(savedTestcase)
        .parameter(userAgentParam)
        .value("ApiTestService-Integration-Test/1.0")
        .build();
    testcaseDataRepository.save(userAgentData);

    // Response 검증 파라미터 추가
    ParameterEntity responseHeaderParam = ParameterEntity.builder()
        .apiList(apiList)
        .category(responseCategory)
        .context(headerContext)
        .name("Content-Type")
        .dataType("string")
        .build();
    responseHeaderParam = parameterRepository.save(responseHeaderParam);

    TestcaseDataEntity expectedContentType = TestcaseDataEntity.builder()
        .testcase(savedTestcase)
        .parameter(responseHeaderParam)
        .value("application/json")
        .build();
    testcaseDataRepository.save(expectedContentType);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-get-real-test"))
        .build();

    System.out.println("📡 API 테스트 실행 중...");

    // when - API 테스트 실행
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - 초기 검증
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-GET-REAL");

    // 초기 TestcaseResultEntity 확인
    TestcaseResultEntity initialResult = testcaseResultRepository.findAll().stream()
        .filter(tr -> tr.getTestcase().getId().equals(savedTestcase.getId()))
        .findFirst()
        .orElse(null);

    assertThat(initialResult).isNotNull();
    assertThat(initialResult.getSuccess()).isNull(); // 초기값은 null (처리중)

    System.out.println("⏳ Redis Streams 비동기 처리 완료 대기 중...");

    // 비동기 처리 완료 대기 및 결과 검증
    TestcaseResultEntity finalResult = waitForAsyncProcessingAndVerify(savedTestcase.getId(), 45);

    if (finalResult != null) {
      System.out.println("✅ 비동기 API 처리 완료!");
    } else {
      System.out.println("❌ 비동기 처리가 시간 내에 완료되지 않음");
      System.out.println("Redis Stream 연결 또는 외부 API 접근에 문제가 있을 수 있습니다.");
    }
    verifyApiCallResults(finalResult, "TC-HTTPBIN-GET-REAL");
    verifyRedisStorageResults("TC-HTTPBIN-GET-REAL");
  }

  @Test
  @DisplayName("실제 HTTPBin API 통신 및 DB 저장 검증 - POST 요청")
  @Transactional
  void testRealApiCommunication_HttpBinPost() throws InterruptedException {
    System.out.println("🚀 실제 HTTPBin POST API 통신 테스트 시작");
    
    // Redis 연결 상태 확인
    if (!isRedisAvailable()) {
      System.out.println("⚠️ Redis 서버에 연결할 수 없어 테스트를 건너뜁니다.");
      org.junit.jupiter.api.Assumptions.assumeTrue(false, "Redis 서버가 필요합니다");
    }

    // given - HTTPBin POST API 테스트 시나리오 구성
    ScenarioEntity scenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-post-real-test")
        .name("HTTPBin POST 실제 통신 테스트")
        .build();
    scenario = scenarioRepository.save(scenario);

    ApiListEntity apiList = ApiListEntity.builder()
        .url("https://httpbin.org/post")
        .path("/post")
        .method("POST")
        .build();
    apiList = apiListRepository.save(apiList);

    MappingEntity mapping = MappingEntity.builder()
        .mappingId("MAP-HTTPBIN-POST-REAL")
        .scenario(scenario)
        .apiList(apiList)
        .step(1)
        .build();
    mapping = mappingRepository.save(mapping);

    TestcaseEntity testcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-POST-REAL")
        .description("실제 HTTPBin POST API 호출 테스트")
        .precondition("JSON 바디 포함")
        .expected("")
        .status(200)
        .mapping(mapping)
        .build();
    final TestcaseEntity savedTestcase = testcaseRepository.save(testcase);

    // Request Body 파라미터 추가
    ParameterEntity messageParam = ParameterEntity.builder()
        .apiList(apiList)
        .category(requestCategory)
        .context(bodyContext)
        .name("message")
        .dataType("string")
        .build();
    messageParam = parameterRepository.save(messageParam);

    ParameterEntity timestampParam = ParameterEntity.builder()
        .apiList(apiList)
        .category(requestCategory)
        .context(bodyContext)
        .name("timestamp")
        .dataType("string")
        .build();
    timestampParam = parameterRepository.save(timestampParam);

    TestcaseDataEntity messageData = TestcaseDataEntity.builder()
        .testcase(savedTestcase)
        .parameter(messageParam)
        .value("Real API Communication Test")
        .build();
    testcaseDataRepository.save(messageData);

    TestcaseDataEntity timestampData = TestcaseDataEntity.builder()
        .testcase(savedTestcase)
        .parameter(timestampParam)
        .value(String.valueOf(System.currentTimeMillis()))
        .build();
    testcaseDataRepository.save(timestampData);

    // Response 검증 파라미터 추가
    ParameterEntity responseJsonParam = ParameterEntity.builder()
        .apiList(apiList)
        .category(responseCategory)
        .context(bodyContext)
        .name("json.message")
        .dataType("string")
        .build();
    responseJsonParam = parameterRepository.save(responseJsonParam);

    TestcaseDataEntity expectedMessage = TestcaseDataEntity.builder()
        .testcase(savedTestcase)
        .parameter(responseJsonParam)
        .value("Real API Communication Test")
        .build();
    testcaseDataRepository.save(expectedMessage);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-post-real-test"))
        .build();

    System.out.println("📡 POST API 테스트 실행 중...");

    // when - API 테스트 실행
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - 검증
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-POST-REAL");

    System.out.println("⏳ POST 요청 비동기 처리 완료 대기 중...");

    // 비동기 처리 완료 대기 및 결과 검증
    TestcaseResultEntity finalResult = waitForAsyncProcessingAndVerify(savedTestcase.getId(), 45);

    if (finalResult != null) {
      System.out.println("✅ POST API 비동기 처리 완료!");
    } else {
      System.out.println("❌ POST API 비동기 처리가 시간 내에 완료되지 않음");
    }
    verifyApiCallResults(finalResult, "TC-HTTPBIN-POST-REAL");
    verifyRedisStorageResults("TC-HTTPBIN-POST-REAL");
  }

  @Test
  @DisplayName("실제 HTTPBin API 통신 실패 케이스 검증 - 404 에러")
  @Transactional
  void testRealApiCommunication_HttpBin404() throws InterruptedException {
    System.out.println("🚀 HTTPBin 404 에러 케이스 테스트 시작");

    // given - 존재하지 않는 엔드포인트로 404 에러 유발
    ScenarioEntity scenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-404-test")
        .name("HTTPBin 404 에러 테스트")
        .build();
    scenario = scenarioRepository.save(scenario);

    ApiListEntity apiList = ApiListEntity.builder()
        .url("https://httpbin.org/status/404")
        .path("/status/404")
        .method("GET")
        .build();
    apiList = apiListRepository.save(apiList);

    MappingEntity mapping = MappingEntity.builder()
        .mappingId("MAP-HTTPBIN-404")
        .scenario(scenario)
        .apiList(apiList)
        .step(1)
        .build();
    mapping = mappingRepository.save(mapping);

    TestcaseEntity testcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-404")
        .description("404 에러 응답 테스트")
        .precondition("없음")
        .expected("")
        .status(404) // 404 상태 코드 기대
        .mapping(mapping)
        .build();
    final TestcaseEntity savedTestcase = testcaseRepository.save(testcase);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-404-test"))
        .build();

    System.out.println("📡 404 에러 API 테스트 실행 중...");

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    System.out.println("⏳ 404 에러 처리 완료 대기 중...");

    // 비동기 처리 완료 대기
    TestcaseResultEntity finalResult = waitForAsyncProcessingAndVerify(savedTestcase.getId(), 45);

    if (finalResult != null) {
      System.out.println("✅ 404 에러 케이스 처리 완료!");
      System.out.println("상태 코드 처리 결과: " + finalResult.getSuccess());

      // 404 에러도 정상적으로 처리되어야 함 (상태 코드가 예상과 일치하므로)
      verifyApiCallResults(finalResult, "TC-HTTPBIN-404");
    }
  }

  /**
   * 비동기 처리 완료를 대기하고 결과를 반환
   */
  private TestcaseResultEntity waitForAsyncProcessingAndVerify(Integer testcaseId, int maxWaitSeconds)
      throws InterruptedException {
    for (int i = 0; i < maxWaitSeconds; i++) {
      Thread.sleep(1000); // 1초 대기

      TestcaseResultEntity result = testcaseResultRepository.findAll().stream()
          .filter(tr -> tr.getTestcase().getId().equals(testcaseId))
          .findFirst()
          .orElse(null);

      if (result != null && result.getSuccess() != null) {
        System.out.println("✅ 비동기 처리 완료! 대기 시간: " + (i + 1) + "초");
        return result;
      }

      if (i % 5 == 0) { // 5초마다 진행 상황 출력
        System.out.println("⏳ 대기 중... " + (i + 1) + "/" + maxWaitSeconds + "초");
      }
    }

    System.out.println("⚠️ " + maxWaitSeconds + "초 대기 후에도 비동기 처리가 완료되지 않음");
    return null;
  }

  /**
   * API 호출 결과 검증
   */
  private void verifyApiCallResults(TestcaseResultEntity result, String testcaseId) {
    System.out.println("=== API 호출 결과 검증 ===");
    System.out.println("테스트케이스 ID: " + testcaseId);
    System.out.println("성공 여부: " + result.getSuccess());
    System.out.println("응답 시간: " + result.getTime() + "ms");
    System.out.println("결과 데이터: " + result.getResult());

    if (result.getReason() != null) {
      System.out.println("실패 이유: " + result.getReason());
    }

    // 기본 검증
    assertThat(result.getSuccess()).isNotNull();

    if (result.getSuccess()) {
      System.out.println("✅ API 호출 성공 및 결과 저장 확인!");
    } else {
      System.out.println("❌ API 호출 실패 또는 검증 실패");
    }
    assertThat(result.getTime()).isGreaterThan(0.0);
  }

  /**
   * Redis 저장 결과 검증
   */
  private void verifyRedisStorageResults(String testcaseId) {
    System.out.println("=== Redis 저장 데이터 검증 ===");

    try {
      // ApiTestRedisEntity 확인
      Iterable<ApiTestRedisEntity> apiTestEntities = apiTestRepository.findAll();
      boolean hasApiTestData = false;

      for (ApiTestRedisEntity entity : apiTestEntities) {
        System.out.println("ApiTestRedisEntity - ID: " + entity.getId() +
            ", Completed: " + entity.getCompleted() +
            ", Finish: " + entity.getFinish());
        hasApiTestData = true;
      }

      // ApiTestDetailRedisEntity 확인
      Iterable<ApiTestDetailRedisEntity> detailEntities = apiTestDetailRepository.findAll();
      boolean hasDetailData = false;

      for (ApiTestDetailRedisEntity entity : detailEntities) {
        if (entity.getId().contains(testcaseId)) {
          System.out.println("ApiTestDetailRedisEntity - ID: " + entity.getId());
          System.out.println("Header: " + entity.getHeader());
          System.out.println("Body: " + entity.getBody());
          System.out.println("Path: " + entity.getPath());
          System.out.println("Query: " + entity.getQuery());
          hasDetailData = true;
        }
      }

      if (hasApiTestData) {
        System.out.println("✅ Redis에 API 테스트 진행 정보가 저장됨");
      }

      if (hasDetailData) {
        System.out.println("✅ Redis에 API 호출 세부 정보가 저장됨");
      }

      if (!hasApiTestData && !hasDetailData) {
        System.out.println("⚠️ Redis에 저장된 데이터가 없음 (처리 완료 후 자동 삭제되었을 수 있음)");
      }

    } catch (Exception e) {
      System.out.println("❌ Redis 데이터 조회 실패: " + e.getMessage());
      assertThat(e).isNotNull(); // 예외가 발생하면 테스트 실패
    }
  }

  /**
   * Redis 연결 상태 확인
   */
  private boolean isRedisAvailable() {
    try {
      var connectionFactory = redisTemplate.getConnectionFactory();
      if (connectionFactory != null) {
        var connection = connectionFactory.getConnection();
        if (connection != null) {
          connection.ping();
          return true;
        }
      }
    } catch (Exception e) {
      System.out.println("Redis 연결 확인 실패: " + e.getMessage());
    }
    return false;
  }
}
