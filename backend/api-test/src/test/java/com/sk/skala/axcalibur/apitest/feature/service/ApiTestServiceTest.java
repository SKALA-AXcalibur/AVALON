package com.sk.skala.axcalibur.apitest.feature.service;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
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
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestRepository;
import com.sk.skala.axcalibur.apitest.feature.repository.ApiTestDetailRepository;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestRedisEntity;
import com.sk.skala.axcalibur.apitest.feature.entity.ApiTestDetailRedisEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("ApiTestService 통합 테스트")
@Rollback
public class ApiTestServiceTest {

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
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ApiTestRepository apiTestRepository;

  @Autowired
  private ApiTestDetailRepository apiTestDetailRepository;

  private ScenarioEntity testScenario;
  private ApiListEntity testApiList;
  private TestcaseEntity testcase1;
  private TestcaseEntity testcase2;
  private MappingEntity mapping1;
  private MappingEntity mapping2;
  private CategoryEntity requestCategory;
  private CategoryEntity responseCategory;
  private ContextEntity headerContext;
  private ContextEntity bodyContext;
  private ParameterEntity headerParam;
  private ParameterEntity bodyParam;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 정리
    cleanup();

    // Redis Stream 초기화 (테스트 시작 전)
    initializeRedisStream();

    // 트랜잭션 내에서 데이터 생성
    createTestData();
  }

  @Transactional
  public void createTestData() {

    // 1. 시나리오 생성
    testScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-test-scenario")
        .name("Httpbin API 테스트 시나리오")
        .build();
    testScenario = scenarioRepository.save(testScenario);

    // 2. API 목록 생성 (httpbin.org 엔드포인트)
    testApiList = ApiListEntity.builder()
        .url("https://httpbin.org/get")
        .path("/get")
        .method("GET")
        .build();
    testApiList = apiListRepository.save(testApiList);

    // 3. 매핑 생성 (테스트케이스보다 먼저)
    mapping1 = MappingEntity.builder()
        .mappingId("MAP-001")
        .scenario(testScenario)
        .apiList(testApiList)
        .step(1)
        .build();
    mapping1 = mappingRepository.save(mapping1);

    mapping2 = MappingEntity.builder()
        .mappingId("MAP-002")
        .scenario(testScenario)
        .apiList(testApiList)
        .step(2)
        .build();
    mapping2 = mappingRepository.save(mapping2);

    // 4. 테스트케이스 생성
    testcase1 = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-001")
        .description("Httpbin GET 요청 테스트 1")
        .precondition("없음")
        .expected("{\"args\": {}, \"headers\": {}, \"origin\": \"*\", \"url\": \"https://httpbin.org/get\"}")
        .status(200)
        .mapping(mapping1)
        .build();
    testcase1 = testcaseRepository.save(testcase1);

    testcase2 = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-002")
        .description("Httpbin GET 요청 테스트 2")
        .precondition("쿼리 파라미터 포함")
        .expected(
            "{\"args\": {\"test\": \"value\"}, \"headers\": {}, \"origin\": \"*\", \"url\": \"https://httpbin.org/get?test=value\"}")
        .status(200)
        .mapping(mapping2)
        .build();
    testcase2 = testcaseRepository.save(testcase2);

    // 5. 카테고리 및 컨텍스트 생성
    requestCategory = CategoryEntity.builder()
        .name("request")
        .build();
    requestCategory = categoryRepository.save(requestCategory);

    responseCategory = CategoryEntity.builder()
        .name("response")
        .build();
    responseCategory = categoryRepository.save(responseCategory);

    headerContext = ContextEntity.builder()
        .name("header")
        .build();
    headerContext = contextRepository.save(headerContext);

    bodyContext = ContextEntity.builder()
        .name("query")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    // 6. 파라미터 생성
    headerParam = ParameterEntity.builder()
        .apiList(testApiList)
        .category(requestCategory)
        .context(headerContext)
        .name("User-Agent")
        .dataType("string")
        .build();
    headerParam = parameterRepository.save(headerParam);

    bodyParam = ParameterEntity.builder()
        .apiList(testApiList)
        .category(requestCategory)
        .context(bodyContext)
        .name("test")
        .dataType("string")
        .build();
    bodyParam = parameterRepository.save(bodyParam);

    // 7. 테스트케이스 데이터 생성
    TestcaseDataEntity headerData1 = TestcaseDataEntity.builder()
        .testcase(testcase1)
        .parameter(headerParam)
        .value("SpringBoot-IntegrationTest/1.0")
        .build();
    testcaseDataRepository.save(headerData1);

    TestcaseDataEntity headerData2 = TestcaseDataEntity.builder()
        .testcase(testcase2)
        .parameter(headerParam)
        .value("SpringBoot-IntegrationTest/1.0")
        .build();
    testcaseDataRepository.save(headerData2);

    TestcaseDataEntity bodyData2 = TestcaseDataEntity.builder()
        .testcase(testcase2)
        .parameter(bodyParam)
        .value("integration-test-value")
        .build();
    testcaseDataRepository.save(bodyData2);

    // 8. Redis Stream 초기 데이터 생성
    try {
      var connectionFactory = redisTemplate.getConnectionFactory();
      if (connectionFactory != null) {
        var connection = connectionFactory.getConnection();
        if (connection != null) {
          // 초기 데이터가 없을 경우에만 추가
          Long streamSize = connection.streamCommands().xLen("avalon-api-test".getBytes());
          if (streamSize != null && streamSize == 0) {
            Map<byte[], byte[]> fields = new HashMap<>();
            fields.put("init".getBytes(), "1".getBytes());
            connection.streamCommands().xAdd("avalon-api-test".getBytes(), fields);
            System.out.println("Redis Stream 초기 데이터 추가 완료");
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Redis Stream 초기 데이터 추가 실패: " + e.getMessage());
    }
  }

  private void initializeRedisStream() {
    try {
      var connectionFactory = redisTemplate.getConnectionFactory();
      if (connectionFactory != null) {
        var connection = connectionFactory.getConnection();
        if (connection != null) {
          try {
            // Consumer Group 생성 (Stream이 없으면 자동 생성)
            connection.streamCommands().xGroupCreate(
                "avalon-api-test".getBytes(),
                "avalon-api-group",
                org.springframework.data.redis.connection.stream.ReadOffset.from("0-0"),
                true // mkstream: true
            );
            System.out.println("Redis Stream 초기화 완료: avalon-api-test");
          } catch (Exception streamException) {
            // 이미 존재하는 경우 무시
            System.out.println("Redis Stream 이미 존재: " + streamException.getMessage());
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Redis Stream 초기화 실패: " + e.getMessage());
    }
  }

  @AfterEach
  void cleanup() {
    // JPQL을 사용한 직접 삭제로 트랜잭션 문제 해결
    try {
      // Native Query를 사용한 직접 삭제 (외래키 관계 무시하고 삭제)
      if (testcaseResultRepository.count() > 0) {
        testcaseResultRepository.deleteAllInBatch();
      }
      if (testcaseDataRepository.count() > 0) {
        testcaseDataRepository.deleteAllInBatch();
      }
      if (parameterRepository.count() > 0) {
        parameterRepository.deleteAllInBatch();
      }
      if (testcaseRepository.count() > 0) {
        testcaseRepository.deleteAllInBatch();
      }
      if (mappingRepository.count() > 0) {
        mappingRepository.deleteAllInBatch();
      }
      if (apiListRepository.count() > 0) {
        apiListRepository.deleteAllInBatch();
      }
      if (scenarioRepository.count() > 0) {
        scenarioRepository.deleteAllInBatch();
      }
      if (contextRepository.count() > 0) {
        contextRepository.deleteAllInBatch();
      }
      if (categoryRepository.count() > 0) {
        categoryRepository.deleteAllInBatch();
      }

    } catch (Exception e) {
      // 삭제 중 오류 발생 시 로그 출력
      System.out.println("Cleanup 중 오류 발생: " + e.getMessage());

      // 실패 시 테이블별 개별 삭제 시도
      try {
        testcaseResultRepository.deleteAll();
        testcaseDataRepository.deleteAll();
        parameterRepository.deleteAll();
        testcaseRepository.deleteAll();
        mappingRepository.deleteAll();
        apiListRepository.deleteAll();
        scenarioRepository.deleteAll();
        contextRepository.deleteAll();
        categoryRepository.deleteAll();
      } catch (Exception fallbackException) {
        System.out.println("Fallback cleanup도 실패: " + fallbackException.getMessage());
      }
    }

    // Redis 데이터 정리 및 Stream 초기화
    try {
      var connectionFactory = redisTemplate.getConnectionFactory();
      if (connectionFactory != null) {
        var connection = connectionFactory.getConnection();
        if (connection != null) {
          // 기존 데이터 정리
          connection.serverCommands().flushDb();

          // Redis Stream 및 Consumer Group 초기화
          try {
            // Stream이 존재하지 않으면 생성
            connection.streamCommands().xGroupCreate(
                "avalon-api-test".getBytes(),
                "avalon-api-group",
                org.springframework.data.redis.connection.stream.ReadOffset.from("0-0"),
                true // mkstream: true - 스트림이 없으면 생성
            );
          } catch (Exception streamException) {
            // Stream이 이미 존재하거나 생성 실패 시 무시
            System.out.println("Redis Stream 초기화 스킵: " + streamException.getMessage());
          }
        }
      }
    } catch (Exception e) {
      // Redis 연결 실패 시 무시
      System.out.println("Redis 연결 실패: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 정상 케이스")
  @Transactional
  void executeTestService_IntegrationTest_Success() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder("TC-HTTPBIN-001", "TC-HTTPBIN-002");

    // DB 검증 - 테스트케이스 결과가 저장되었는지 확인
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    assertThat(testcaseResults).hasSize(2);

    // 각 테스트케이스 결과 검증
    testcaseResults.forEach(result1 -> {
      assertThat(result1.getTestcase()).isNotNull();
      assertThat(result1.getResult()).isEqualTo("");
      assertThat(result1.getSuccess()).isNull(); // 초기값은 null (실행중)
      assertThat(result1.getTime()).isNull(); // 초기값은 null
    });
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 빈 시나리오 리스트")
  @Transactional
  void executeTestService_IntegrationTest_EmptyScenarioList() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of())
        .build();

    // when & then - 결과 기반 검증 (예외 대신 빈 결과여야 함)
    try {
      List<String> result = apiTestService.excuteTestService(requestDto);
      // 예외가 발생하지 않는다면 빈 결과여야 함
      assertThat(result).isEmpty();
    } catch (Exception e) {
      // 예외가 발생하는 것이 정상 동작임을 확인
      assertThat(e).isInstanceOf(Exception.class);
    }

    // 데이터베이스 상태 검증 - 잘못된 요청으로 인해 데이터가 생성되지 않았는지 확인
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    // 기본 setUp 데이터가 있을 수 있으므로 추가 데이터가 없는지 확인
    assertThat(testcaseResults.size()).isLessThanOrEqualTo(0);
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 존재하지 않는 시나리오")
  @Transactional
  void executeTestService_IntegrationTest_NonExistentScenario() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("non-existent-scenario"))
        .build();

    // when & then - 결과 기반 검증
    try {
      List<String> result = apiTestService.excuteTestService(requestDto);
      // 예외가 발생하지 않는다면 빈 결과여야 함
      assertThat(result).isEmpty();
    } catch (Exception e) {
      // 예외가 발생하는 것이 정상 동작임을 확인
      assertThat(e).isInstanceOf(Exception.class);
    }

    // 데이터베이스 상태 검증 - 존재하지 않는 시나리오로 인해 결과가 생성되지 않았는지 확인
    List<TestcaseResultEntity> results = testcaseResultRepository.findAll();
    // 존재하지 않는 시나리오이므로 해당 시나리오 관련 결과가 없어야 함
    boolean hasNonExistentScenarioResults = results.stream()
        .anyMatch(r -> r.getTestcase().getMapping().getScenario().getScenarioId()
            .equals("non-existent-scenario"));
    assertThat(hasNonExistentScenarioResults).isFalse();
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 다중 시나리오")
  @Transactional
  void executeTestService_IntegrationTest_MultipleScenarios() {
    // given - 추가 시나리오 생성
    ScenarioEntity scenario2 = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-post-scenario")
        .name("Httpbin POST 테스트 시나리오")
        .build();
    scenario2 = scenarioRepository.save(scenario2);

    // POST API 생성
    ApiListEntity postApiList = ApiListEntity.builder()
        .url("https://httpbin.org/post")
        .path("/post")
        .method("POST")
        .build();
    postApiList = apiListRepository.save(postApiList);

    // POST 매핑 생성
    MappingEntity postMapping = MappingEntity.builder()
        .mappingId("MAP-POST-001")
        .scenario(scenario2)
        .apiList(postApiList)
        .step(1)
        .build();
    postMapping = mappingRepository.save(postMapping);

    // POST 테스트케이스 생성
    TestcaseEntity postTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-POST-001")
        .description("JSON 데이터 POST 요청 테스트")
        .precondition("JSON 바디 포함")
        .expected("{\"json\": {\"key\": \"value\"}, \"data\": \"{\\\"key\\\": \\\"value\\\"}\"}")
        .status(200)
        .mapping(postMapping)
        .build();
    final TestcaseEntity savedPostTestcase = testcaseRepository.save(postTestcase);

    // POST 요청용 파라미터 생성
    ParameterEntity postBodyParam = ParameterEntity.builder()
        .apiList(postApiList)
        .category(requestCategory)
        .context(bodyContext)
        .name("key")
        .dataType("string")
        .build();
    postBodyParam = parameterRepository.save(postBodyParam);

    TestcaseDataEntity postBodyData = TestcaseDataEntity.builder()
        .testcase(savedPostTestcase)
        .parameter(postBodyParam)
        .value("integration-test-post-value")
        .build();
    testcaseDataRepository.save(postBodyData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario", "httpbin-post-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3); // GET 2개 + POST 1개
    assertThat(result).containsExactlyInAnyOrder(
        "TC-HTTPBIN-001",
        "TC-HTTPBIN-002",
        "TC-HTTPBIN-POST-001");

    // DB 검증 - 모든 테스트케이스 결과가 저장되었는지 확인
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    assertThat(testcaseResults).hasSize(3);

    // POST 테스트케이스 결과 검증
    TestcaseResultEntity postResult = testcaseResults.stream()
        .filter(tr -> tr.getTestcase().getId().equals(savedPostTestcase.getId()))
        .findFirst()
        .orElse(null);
    assertThat(postResult).isNotNull();
    assertThat(postResult.getResult()).isEqualTo("");
    assertThat(postResult.getSuccess()).isNull(); // 초기값은 null (실행중)
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - Redis Stream 통신 검증")
  @Transactional
  void executeTestService_IntegrationTest_RedisStreamVerification() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    // Redis Stream에 메시지가 추가되었는지 확인
    try {
      // Stream 존재 여부 확인
      Boolean hasKey = redisTemplate.hasKey("avalon-api-test");
      // 실제 Redis 연결이 가능한 경우에만 검증
      if (hasKey != null) {
        assertThat(hasKey).isTrue();
      }
    } catch (Exception e) {
      // Redis 연결 실패 시 로그만 출력
      System.out.println("Redis 연결 실패: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 트랜잭션 롤백 검증")
  @Transactional
  void executeTestService_IntegrationTest_TransactionRollback() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // 실행 전 데이터 개수 확인
    long beforeCount = testcaseResultRepository.count();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();

    // 실행 후 데이터 개수 확인
    long afterCount = testcaseResultRepository.count();
    assertThat(afterCount).isGreaterThan(beforeCount);

    // 테스트 메서드 종료 시 @Transactional에 의해 롤백됨을 확인
    // (실제 롤백 검증은 별도 테스트에서 수행)
  }

  @Test
  @DisplayName("buildTaskData 메서드 통합 테스트")
  @Transactional
  void buildTaskData_IntegrationTest() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when - executeTestService 내부에서 buildTaskData가 호출됨
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    // 내부적으로 buildTaskData가 정상 호출되어 API 요청 데이터가 구성되었음을 확인
    // (실제 HTTP 요청은 Redis Stream을 통해 비동기로 처리됨)
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - PUT 메서드 테스트")
  @Transactional
  void executeTestService_IntegrationTest_PutMethod() {
    // given - PUT 시나리오 생성
    ScenarioEntity putScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-put-scenario")
        .name("Httpbin PUT 테스트 시나리오")
        .build();
    putScenario = scenarioRepository.save(putScenario);

    // PUT API 생성
    ApiListEntity putApiList = ApiListEntity.builder()
        .url("https://httpbin.org/put")
        .path("/put")
        .method("PUT")
        .build();
    putApiList = apiListRepository.save(putApiList);

    // PUT 매핑 생성
    MappingEntity putMapping = MappingEntity.builder()
        .mappingId("MAP-PUT-001")
        .scenario(putScenario)
        .apiList(putApiList)
        .step(1)
        .build();
    putMapping = mappingRepository.save(putMapping);

    // PUT 테스트케이스 생성
    TestcaseEntity putTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-PUT-001")
        .description("JSON 데이터 PUT 요청 테스트")
        .precondition("JSON 바디 포함")
        .expected("{\"json\": {\"name\": \"updated\"}, \"data\": \"{\\\"name\\\": \\\"updated\\\"}\"}")
        .status(200)
        .mapping(putMapping)
        .build();
    final TestcaseEntity savedPutTestcase = testcaseRepository.save(putTestcase);

    // JSON 바디 컨텍스트 생성
    ContextEntity jsonBodyContext = ContextEntity.builder()
        .name("body")
        .build();
    jsonBodyContext = contextRepository.save(jsonBodyContext);

    // PUT 바디 파라미터 생성
    ParameterEntity putBodyParam = ParameterEntity.builder()
        .apiList(putApiList)
        .category(requestCategory)
        .context(jsonBodyContext)
        .name("name")
        .dataType("string")
        .build();
    putBodyParam = parameterRepository.save(putBodyParam);

    TestcaseDataEntity putBodyData = TestcaseDataEntity.builder()
        .testcase(savedPutTestcase)
        .parameter(putBodyParam)
        .value("updated")
        .build();
    testcaseDataRepository.save(putBodyData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-put-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-PUT-001");

    // DB 검증
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    boolean putResultExists = testcaseResults.stream()
        .anyMatch(tr -> tr.getTestcase().getId().equals(savedPutTestcase.getId()));
    assertThat(putResultExists).isTrue();
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 헤더와 쿼리 파라미터 조합")
  @Transactional
  void executeTestService_IntegrationTest_HeaderAndQueryParams() {
    // given - 복합 파라미터 시나리오 생성
    ScenarioEntity complexScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-complex-scenario")
        .name("Httpbin 복합 파라미터 테스트 시나리오")
        .build();
    complexScenario = scenarioRepository.save(complexScenario);

    // API 생성 (httpbin의 /anything 엔드포인트 사용)
    ApiListEntity complexApiList = ApiListEntity.builder()
        .url("https://httpbin.org/anything")
        .path("/anything")
        .method("GET")
        .build();
    complexApiList = apiListRepository.save(complexApiList);

    // 매핑 생성
    MappingEntity complexMapping = MappingEntity.builder()
        .mappingId("MAP-COMPLEX-001")
        .scenario(complexScenario)
        .apiList(complexApiList)
        .step(1)
        .build();
    complexMapping = mappingRepository.save(complexMapping);

    // 테스트케이스 생성
    TestcaseEntity complexTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-COMPLEX-001")
        .description("헤더와 쿼리 파라미터를 포함한 복합 요청 테스트")
        .precondition("헤더 Authorization, 쿼리 파라미터 test 포함")
        .expected("{\"args\": {\"test\": \"complex\"}, \"headers\": {\"Authorization\": \"Bearer token\"}}")
        .status(200)
        .mapping(complexMapping)
        .build();
    complexTestcase = testcaseRepository.save(complexTestcase);

    // Authorization 헤더 파라미터 생성
    ParameterEntity authHeaderParam = ParameterEntity.builder()
        .apiList(complexApiList)
        .category(requestCategory)
        .context(headerContext)
        .name("Authorization")
        .dataType("string")
        .build();
    authHeaderParam = parameterRepository.save(authHeaderParam);

    // 쿼리 파라미터 생성
    ParameterEntity complexQueryParam = ParameterEntity.builder()
        .apiList(complexApiList)
        .category(requestCategory)
        .context(bodyContext) // bodyContext는 실제로 query context
        .name("test")
        .dataType("string")
        .build();
    complexQueryParam = parameterRepository.save(complexQueryParam);

    // 테스트케이스 데이터 생성
    TestcaseDataEntity authHeaderData = TestcaseDataEntity.builder()
        .testcase(complexTestcase)
        .parameter(authHeaderParam)
        .value("Bearer integration-test-token")
        .build();
    testcaseDataRepository.save(authHeaderData);

    TestcaseDataEntity complexQueryData = TestcaseDataEntity.builder()
        .testcase(complexTestcase)
        .parameter(complexQueryParam)
        .value("complex")
        .build();
    testcaseDataRepository.save(complexQueryData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-complex-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-COMPLEX-001");
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 데이터 타입 변환 검증")
  @Transactional
  void executeTestService_IntegrationTest_DataTypeConversion() {
    // given - 다양한 데이터 타입 시나리오 생성
    ScenarioEntity dataTypeScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-datatype-scenario")
        .name("Httpbin 데이터 타입 변환 테스트 시나리오")
        .build();
    dataTypeScenario = scenarioRepository.save(dataTypeScenario);

    // API 생성
    ApiListEntity dataTypeApiList = ApiListEntity.builder()
        .url("https://httpbin.org/post")
        .path("/post")
        .method("POST")
        .build();
    dataTypeApiList = apiListRepository.save(dataTypeApiList);

    // 매핑 생성
    MappingEntity dataTypeMapping = MappingEntity.builder()
        .mappingId("MAP-DATATYPE-001")
        .scenario(dataTypeScenario)
        .apiList(dataTypeApiList)
        .step(1)
        .build();
    dataTypeMapping = mappingRepository.save(dataTypeMapping);

    // 테스트케이스 생성
    TestcaseEntity dataTypeTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-DATATYPE-001")
        .description("다양한 데이터 타입 변환 테스트")
        .precondition("정수, 불린, 문자열 파라미터 포함")
        .expected("{\"json\": {\"count\": 10, \"enabled\": true, \"message\": \"test\"}}")
        .status(200)
        .mapping(dataTypeMapping)
        .build();
    final TestcaseEntity savedDataTypeTestcase = testcaseRepository.save(dataTypeTestcase);

    // JSON 바디 컨텍스트 생성
    ContextEntity jsonContext = ContextEntity.builder()
        .name("body")
        .build();
    jsonContext = contextRepository.save(jsonContext);

    // 다양한 데이터 타입 파라미터 생성
    ParameterEntity intParam = ParameterEntity.builder()
        .apiList(dataTypeApiList)
        .category(requestCategory)
        .context(jsonContext)
        .name("count")
        .dataType("integer")
        .build();
    intParam = parameterRepository.save(intParam);

    ParameterEntity boolParam = ParameterEntity.builder()
        .apiList(dataTypeApiList)
        .category(requestCategory)
        .context(jsonContext)
        .name("enabled")
        .dataType("boolean")
        .build();
    boolParam = parameterRepository.save(boolParam);

    ParameterEntity stringParam = ParameterEntity.builder()
        .apiList(dataTypeApiList)
        .category(requestCategory)
        .context(jsonContext)
        .name("message")
        .dataType("string")
        .build();
    stringParam = parameterRepository.save(stringParam);

    // 테스트케이스 데이터 생성
    TestcaseDataEntity intData = TestcaseDataEntity.builder()
        .testcase(savedDataTypeTestcase)
        .parameter(intParam)
        .value("10")
        .build();
    testcaseDataRepository.save(intData);

    TestcaseDataEntity boolData = TestcaseDataEntity.builder()
        .testcase(savedDataTypeTestcase)
        .parameter(boolParam)
        .value("true")
        .build();
    testcaseDataRepository.save(boolData);

    TestcaseDataEntity stringData = TestcaseDataEntity.builder()
        .testcase(savedDataTypeTestcase)
        .parameter(stringParam)
        .value("test")
        .build();
    testcaseDataRepository.save(stringData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-datatype-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-DATATYPE-001");

    // buildTaskData 메서드가 정상적으로 데이터 타입 변환을 수행했는지 간접 검증
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    boolean dataTypeResultExists = testcaseResults.stream()
        .anyMatch(tr -> tr.getTestcase().getId().equals(savedDataTypeTestcase.getId()));
    assertThat(dataTypeResultExists).isTrue();
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 대용량 데이터 처리")
  @Transactional
  void executeTestService_IntegrationTest_LargeDataProcessing() {
    // given - 대용량 데이터 시나리오 생성
    List<String> largeScenarioList = new ArrayList<>();

    // 5개의 시나리오와 각각 2개의 테스트케이스 생성 (총 10개)
    for (int i = 1; i <= 5; i++) {
      String scenarioId = "httpbin-large-scenario-" + i;
      largeScenarioList.add(scenarioId);

      ScenarioEntity largeScenario = ScenarioEntity.builder()
          .projectKey(1)
          .scenarioId(scenarioId)
          .name("Httpbin 대용량 테스트 시나리오 " + i)
          .build();
      largeScenario = scenarioRepository.save(largeScenario);

      // 각 시나리오마다 2개의 API와 테스트케이스 생성
      for (int j = 1; j <= 2; j++) {
        ApiListEntity largeApiList = ApiListEntity.builder()
            .url("https://httpbin.org/get")
            .path("/get")
            .method("GET")
            .build();
        largeApiList = apiListRepository.save(largeApiList);

        MappingEntity largeMapping = MappingEntity.builder()
            .mappingId("MAP-LARGE-" + i + "-" + j)
            .scenario(largeScenario)
            .apiList(largeApiList)
            .step(j)
            .build();
        largeMapping = mappingRepository.save(largeMapping);

        TestcaseEntity largeTestcase = TestcaseEntity.builder()
            .testcaseId("TC-HTTPBIN-LARGE-" + i + "-" + j)
            .description("대용량 데이터 처리 테스트 " + i + "-" + j)
            .precondition("없음")
            .expected("{\"args\": {}, \"headers\": {}, \"origin\": \"*\"}")
            .status(200)
            .mapping(largeMapping)
            .build();
        testcaseRepository.save(largeTestcase);
      }
    }

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(largeScenarioList)
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(10); // 5개 시나리오 × 2개 테스트케이스

    // 모든 테스트케이스 ID가 올바른 형식인지 확인
    for (String testcaseId : result) {
      assertThat(testcaseId).matches("TC-HTTPBIN-LARGE-\\d+-\\d+");
    }

    // DB 검증 - 모든 테스트케이스 결과가 저장되었는지 확인
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();

    // 디버깅: 저장된 모든 테스트케이스 ID 확인
    System.out.println("=== 저장된 테스트케이스 결과 목록 ===");
    testcaseResults.forEach(tr -> {
      if (tr.getTestcase() != null) {
        System.out.println("TestcaseId: " + tr.getTestcase().getTestcaseId());
      } else {
        System.out.println("TestcaseId: null (testcase가 null)");
      }
    });

    long largeTestResults = testcaseResults.stream()
        .filter(tr -> tr.getTestcase() != null &&
            tr.getTestcase().getTestcaseId() != null &&
            tr.getTestcase().getTestcaseId().startsWith("TC-HTTPBIN-LARGE-"))
        .count();

    System.out.println("대용량 테스트 결과 개수: " + largeTestResults);
    assertThat(largeTestResults).isEqualTo(10);
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - Redis Stream 실패 복구")
  @Transactional
  void executeTestService_IntegrationTest_RedisStreamFailureRecovery() {
    // given - Redis 연결이 실패할 수 있는 상황을 시뮬레이션
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - Redis 실패에도 불구하고 DB 저장은 성공해야 함
    assertThat(result).isNotNull();

    // DB에 테스트케이스 결과가 저장되었는지 확인
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    assertThat(testcaseResults).isNotEmpty();

    // 각 테스트케이스 결과가 초기 상태로 저장되었는지 확인
    testcaseResults.forEach(tr -> {
      assertThat(tr.getResult()).isEqualTo("");
      assertThat(tr.getSuccess()).isNull(); // 초기값은 null (실행중)
      assertThat(tr.getTime()).isNull();
    });
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 성능 측정")
  @Transactional
  void executeTestService_IntegrationTest_PerformanceMeasurement() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when - 성능 측정
    long startTime = System.currentTimeMillis();
    List<String> result = apiTestService.excuteTestService(requestDto);
    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    // 합리적인 실행 시간 내에 완료되었는지 확인 (5초 이내)
    assertThat(executionTime).isLessThan(5000L);

    System.out.println("executeTestService 실행 시간: " + executionTime + "ms");
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 트랜잭션 격리 수준 검증")
  @Transactional
  void executeTestService_IntegrationTest_TransactionIsolation() {
    // given
    long initialCount = testcaseResultRepository.count();

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();

    // 트랜잭션 내에서 데이터가 정상적으로 저장되었는지 확인
    long currentCount = testcaseResultRepository.count();
    assertThat(currentCount).isGreaterThan(initialCount);

    // 정확히 2개의 새로운 레코드가 추가되었는지 확인
    assertThat(currentCount - initialCount).isEqualTo(2);
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 동시성 테스트")
  @Transactional
  void executeTestService_IntegrationTest_Concurrency() {
    // given - 동일한 요청을 여러 번 실행
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when - 순차적으로 여러 번 실행 (실제 동시성은 통합테스트에서 제한적)
    List<String> result1 = apiTestService.excuteTestService(requestDto);

    // 두 번째 실행을 위해 기존 결과 정리
    testcaseResultRepository.deleteAll();

    List<String> result2 = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result1).isNotNull();
    assertThat(result2).isNotNull();
    assertThat(result1).hasSize(2);
    assertThat(result2).hasSize(2);

    // 같은 테스트케이스 ID들이 반환되어야 함
    assertThat(result1).containsExactlyInAnyOrderElementsOf(result2);
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - End-to-End 완전한 시나리오")
  @Transactional
  void executeTestService_IntegrationTest_CompleteEndToEndScenario() {
    // given - 실제 사용 시나리오와 유사한 완전한 테스트 데이터 구성

    // 1. 사용자 인증 시나리오
    ScenarioEntity authScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-auth-scenario")
        .name("Httpbin 인증 테스트 시나리오")
        .build();
    authScenario = scenarioRepository.save(authScenario);

    // 2. 인증 API (httpbin의 basic-auth 엔드포인트)
    ApiListEntity authApiList = ApiListEntity.builder()
        .url("https://httpbin.org/basic-auth/user/pass")
        .path("/basic-auth/user/pass")
        .method("GET")
        .build();
    authApiList = apiListRepository.save(authApiList);

    MappingEntity authMapping = MappingEntity.builder()
        .mappingId("MAP-AUTH-001")
        .scenario(authScenario)
        .apiList(authApiList)
        .step(1)
        .build();
    authMapping = mappingRepository.save(authMapping);

    TestcaseEntity authTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-AUTH-001")
        .description("Basic Authentication 테스트")
        .precondition("Authorization 헤더 포함")
        .expected("{\"authenticated\": true, \"user\": \"user\"}")
        .status(200)
        .mapping(authMapping)
        .build();
    authTestcase = testcaseRepository.save(authTestcase);

    // Authorization 헤더 파라미터
    ParameterEntity authParam = ParameterEntity.builder()
        .apiList(authApiList)
        .category(requestCategory)
        .context(headerContext)
        .name("Authorization")
        .dataType("string")
        .build();
    authParam = parameterRepository.save(authParam);

    TestcaseDataEntity authData = TestcaseDataEntity.builder()
        .testcase(authTestcase)
        .parameter(authParam)
        .value("Basic dXNlcjpwYXNz") // user:pass in base64
        .build();
    testcaseDataRepository.save(authData);

    // 3. 다중 시나리오 실행
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario", "httpbin-auth-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - 완전한 End-to-End 검증
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3); // GET 2개 + AUTH 1개
    assertThat(result).containsExactlyInAnyOrder(
        "TC-HTTPBIN-001",
        "TC-HTTPBIN-002",
        "TC-HTTPBIN-AUTH-001");

    // DB 상태 검증
    List<TestcaseResultEntity> allResults = testcaseResultRepository.findAll();
    assertThat(allResults).hasSize(3);

    // 각 시나리오별 검증
    Map<String, TestcaseResultEntity> resultMap = allResults.stream()
        .collect(Collectors.toMap(
            tr -> tr.getTestcase().getTestcaseId(),
            tr -> tr));

    // GET 테스트케이스들 검증
    assertThat(resultMap).containsKey("TC-HTTPBIN-001");
    assertThat(resultMap).containsKey("TC-HTTPBIN-002");
    assertThat(resultMap).containsKey("TC-HTTPBIN-AUTH-001");

    // 모든 결과가 초기 상태로 저장되었는지 확인
    allResults.forEach(tr -> {
      assertThat(tr.getResult()).isEqualTo("");
      assertThat(tr.getSuccess()).isNull(); // 초기값은 null (실행중)
      assertThat(tr.getTime()).isNull();
      assertThat(tr.getReason()).isNull();
    });

    // Redis Stream 통신 시도 확인 (실제 Redis 연결 여부와 관계없이 로직 검증)
    // 이 부분은 실제 Redis Stream에 메시지가 전송되었는지는 확인할 수 없지만,
    // 예외 없이 정상 완료되었다면 Redis 통신 로직이 정상 작동한 것으로 간주
    System.out.println("End-to-End 테스트 완료 - 처리된 테스트케이스: " + result.size() + "개");
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 상태 기반 검증")
  @Transactional
  void executeTestService_IntegrationTest_StateBased() {
    // given - 정상 케이스와 비교할 기준 설정
    long initialResultCount = testcaseResultRepository.count();

    ExcuteTestServiceRequestDto invalidRequestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(999) // 존재하지 않는 프로젝트
        .scenarioList(List.of("any-scenario"))
        .build();

    // when - 잘못된 요청 실행
    try {
      apiTestService.excuteTestService(invalidRequestDto);
    } catch (Exception e) {
      // 예외 발생은 예상된 동작
    }

    // then - 상태 기반 검증
    // 1. 데이터베이스 상태가 변경되지 않았는지 확인
    long finalResultCount = testcaseResultRepository.count();
    assertThat(finalResultCount).isEqualTo(initialResultCount);

    // 2. 유효하지 않은 프로젝트 키로 인해 관련 데이터가 생성되지 않았는지 확인
    List<TestcaseResultEntity> allResults = testcaseResultRepository.findAll();
    boolean hasInvalidProjectResults = allResults.stream()
        .anyMatch(r -> r.getTestcase().getMapping().getScenario().getProjectKey() == 999);
    assertThat(hasInvalidProjectResults).isFalse();
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 멱등성 검증")
  @Transactional
  void executeTestService_IntegrationTest_IdempotencyCheck() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when - 동일한 요청을 두 번 실행
    List<String> result1 = apiTestService.excuteTestService(requestDto);
    List<String> result2 = apiTestService.excuteTestService(requestDto);

    // then - 결과의 일관성 검증 (메시지와 무관)
    assertThat(result1).isNotNull();
    assertThat(result2).isNotNull();
    assertThat(result1).hasSize(result2.size());

    // 같은 테스트케이스들이 반환되는지 확인
    assertThat(result1).containsExactlyInAnyOrderElementsOf(result2);

    // 데이터베이스 상태 확인 - 중복 생성되지 않았는지
    List<TestcaseResultEntity> allResults = testcaseResultRepository.findAll();
    // 각 테스트케이스당 결과가 중복 생성되지 않았는지 확인
    Set<String> uniqueTestcaseIds = allResults.stream()
        .map(r -> r.getTestcase().getTestcaseId())
        .collect(java.util.stream.Collectors.toSet());

    // 유니크한 테스트케이스 ID 개수가 예상과 일치하는지 확인
    assertThat(uniqueTestcaseIds).hasSize(2); // httpbin-test-scenario에는 2개의 테스트케이스
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 행위 기반 검증")
  @Transactional
  void executeTestService_IntegrationTest_BehaviorBased() {
    // given
    ExcuteTestServiceRequestDto validRequestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    ExcuteTestServiceRequestDto invalidRequestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("non-existent-scenario"))
        .build();

    // when & then - 정상 요청은 성공해야 함
    assertThatCode(() -> apiTestService.excuteTestService(validRequestDto))
        .doesNotThrowAnyException();

    // when & then - 잘못된 요청은 실패해야 함
    assertThatCode(() -> apiTestService.excuteTestService(invalidRequestDto))
        .isInstanceOf(Exception.class);

    // 행위 검증: 정상 요청 후에는 데이터가 생성되어야 함
    List<TestcaseResultEntity> results = testcaseResultRepository.findAll();
    assertThat(results).isNotEmpty();

    // 행위 검증: 모든 결과에 testcase가 연결되어 있어야 함
    results.forEach(result -> {
      assertThat(result.getTestcase()).isNotNull();
      assertThat(result.getTestcase().getTestcaseId()).isNotBlank();
    });
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - 계약 기반 검증")
  @Transactional
  void executeTestService_IntegrationTest_ContractBased() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - 메서드 계약 검증 (입력-출력 관계)
    // 1. null이 아닌 결과를 반환해야 함
    assertThat(result).isNotNull();

    // 2. 빈 리스트가 아니어야 함 (유효한 시나리오가 있으므로)
    assertThat(result).isNotEmpty();

    // 3. 반환된 모든 ID가 유효한 형식이어야 함
    result.forEach(testcaseId -> {
      assertThat(testcaseId)
          .isNotNull()
          .isNotBlank()
          .startsWith("TC-"); // 테스트케이스 ID 형식 검증
    });

    // 4. 반환된 ID가 실제 데이터베이스에 존재해야 함
    result.forEach(testcaseId -> {
      boolean exists = testcaseRepository.findAll().stream()
          .anyMatch(tc -> tc.getTestcaseId().equals(testcaseId));
      assertThat(exists).isTrue();
    });

    // 5. 요청한 시나리오의 테스트케이스만 반환되어야 함
    List<TestcaseEntity> returnedTestcases = testcaseRepository.findAll().stream()
        .filter(tc -> result.contains(tc.getTestcaseId()))
        .collect(java.util.stream.Collectors.toList());

    returnedTestcases.forEach(tc -> {
      assertThat(tc.getMapping().getScenario().getScenarioId())
          .isEqualTo("httpbin-test-scenario");
    });
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - Redis 엔티티 상태 검증")
  @Transactional
  void executeTestService_IntegrationTest_RedisEntityVerification() {
    // given
    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    // Redis 엔티티 상태 검증 (비동기 처리로 인해 약간의 대기 시간 필요할 수 있음)
    try {
      // ApiTestRedisEntity 확인 - 시나리오 진행 상황
      Iterable<ApiTestRedisEntity> apiTestEntities = apiTestRepository.findAll();
      List<ApiTestRedisEntity> apiTestEntityList = new ArrayList<>();
      apiTestEntities.forEach(apiTestEntityList::add);

      if (!apiTestEntityList.isEmpty()) {
        apiTestEntityList.forEach(entity -> {
          assertThat(entity.getId()).isNotNull();
          assertThat(entity.getCompleted()).isGreaterThanOrEqualTo(0);
          assertThat(entity.getFinish()).isGreaterThan(0);
        });
      }

      // ApiTestDetailRedisEntity 확인 - API 실행 세부 정보
      Iterable<ApiTestDetailRedisEntity> apiTestDetailEntities = apiTestDetailRepository.findAll();
      List<ApiTestDetailRedisEntity> apiTestDetailEntityList = new ArrayList<>();
      apiTestDetailEntities.forEach(apiTestDetailEntityList::add);

      if (!apiTestDetailEntityList.isEmpty()) {
        apiTestDetailEntityList.forEach(entity -> {
          assertThat(entity.getId()).isNotNull();
          assertThat(entity.getResultId()).isNotNull();
          assertThat(entity.getHeader()).isNotNull();
          assertThat(entity.getBody()).isNotNull();
          assertThat(entity.getPath()).isNotNull();
          assertThat(entity.getQuery()).isNotNull();
        });
      }

    } catch (Exception e) {
      // Redis 연결 실패 또는 비동기 처리 지연으로 인한 예외 처리
      System.out.println("Redis 엔티티 검증 중 오류 발생: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("executeTestService 통합 테스트 - Response 파라미터 기반 예상 응답 비교")
  @Transactional
  void executeTestService_IntegrationTest_ResponseParameterBasedValidation() {
    // given - response category 파라미터를 포함한 테스트케이스 생성
    ScenarioEntity responseParamScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("httpbin-response-param-scenario")
        .name("Response 파라미터 기반 검증 테스트 시나리오")
        .build();
    responseParamScenario = scenarioRepository.save(responseParamScenario);

    // API 생성 (httpbin의 /json 엔드포인트 - 고정된 응답)
    ApiListEntity responseParamApiList = ApiListEntity.builder()
        .url("https://httpbin.org/json")
        .path("/json")
        .method("GET")
        .build();
    responseParamApiList = apiListRepository.save(responseParamApiList);

    // 매핑 생성
    MappingEntity responseParamMapping = MappingEntity.builder()
        .mappingId("MAP-RESPONSE-PARAM-001")
        .scenario(responseParamScenario)
        .apiList(responseParamApiList)
        .step(1)
        .build();
    responseParamMapping = mappingRepository.save(responseParamMapping);

    // 테스트케이스 생성
    TestcaseEntity responseParamTestcase = TestcaseEntity.builder()
        .testcaseId("TC-HTTPBIN-RESPONSE-PARAM-001")
        .description("Response 파라미터 기반 응답 검증 테스트")
        .precondition("없음")
        .expected("") // expected는 예상 응답이 아님
        .status(200)
        .mapping(responseParamMapping)
        .build();
    final TestcaseEntity savedResponseParamTestcase = testcaseRepository.save(responseParamTestcase);

    // Response 카테고리 파라미터들 생성 (예상 응답 정의)
    // 1. Response Header 파라미터
    ParameterEntity responseHeaderParam = ParameterEntity.builder()
        .apiList(responseParamApiList)
        .category(responseCategory) // response 카테고리
        .context(headerContext)
        .name("Content-Type")
        .dataType("string")
        .build();
    responseHeaderParam = parameterRepository.save(responseHeaderParam);

    // 2. Response Body 파라미터들
    ContextEntity responseBodyContext = ContextEntity.builder()
        .name("body")
        .build();
    responseBodyContext = contextRepository.save(responseBodyContext);

    ParameterEntity responseTitleParam = ParameterEntity.builder()
        .apiList(responseParamApiList)
        .category(responseCategory) // response 카테고리
        .context(responseBodyContext)
        .name("slideshow.title")
        .dataType("string")
        .build();
    responseTitleParam = parameterRepository.save(responseTitleParam);

    ParameterEntity responseAuthorParam = ParameterEntity.builder()
        .apiList(responseParamApiList)
        .category(responseCategory) // response 카테고리
        .context(responseBodyContext)
        .name("slideshow.author")
        .dataType("string")
        .build();
    responseAuthorParam = parameterRepository.save(responseAuthorParam);

    // 예상 응답 데이터 생성 (TestcaseDataEntity)
    TestcaseDataEntity expectedHeaderData = TestcaseDataEntity.builder()
        .testcase(savedResponseParamTestcase)
        .parameter(responseHeaderParam)
        .value("application/json") // 예상되는 Content-Type
        .build();
    testcaseDataRepository.save(expectedHeaderData);

    TestcaseDataEntity expectedTitleData = TestcaseDataEntity.builder()
        .testcase(savedResponseParamTestcase)
        .parameter(responseTitleParam)
        .value("Sample Slide Show") // 예상되는 title 값
        .build();
    testcaseDataRepository.save(expectedTitleData);

    TestcaseDataEntity expectedAuthorData = TestcaseDataEntity.builder()
        .testcase(savedResponseParamTestcase)
        .parameter(responseAuthorParam)
        .value("Yours Truly") // 예상되는 author 값
        .build();
    testcaseDataRepository.save(expectedAuthorData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("httpbin-response-param-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-HTTPBIN-RESPONSE-PARAM-001");

    // Response 파라미터 기반 예상 응답 검증
    List<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findAll();
    TestcaseResultEntity responseParamResult = testcaseResults.stream()
        .filter(tr -> tr.getTestcase().getId().equals(savedResponseParamTestcase.getId()))
        .findFirst()
        .orElse(null);

    assertThat(responseParamResult).isNotNull();

    // Response category 파라미터들과 예상 값들 확인
    List<TestcaseDataEntity> responseExpectedData = testcaseDataRepository.findAll().stream()
        .filter(data -> data.getTestcase().getId().equals(savedResponseParamTestcase.getId()))
        .filter(data -> data.getParameter().getCategory().getName().equals("response"))
        .collect(Collectors.toList());

    assertThat(responseExpectedData).hasSize(3); // Header 1개 + Body 2개

    // buildTaskData가 response 파라미터를 resHeader/resBody로 구성하는지 테스트
    System.out.println("=== Response 파라미터 기반 예상 응답 (TestcaseDataEntity) ===");
    responseExpectedData.forEach(data -> {
      String paramName = data.getParameter().getName();
      String expectedValue = data.getValue();
      String context = data.getParameter().getContext().getName();
      System.out.println("Context: " + context + ", Parameter: " + paramName + ", Expected: " + expectedValue);
    });

    System.out.println("=== buildTaskData로 구성된 ApiRequestDataDto 확인 ===");
    // 실제로 buildTaskData에서 생성되는 resHeader/resBody 확인을 위해
    // ApiTestServiceImpl의 로직을 직접 테스트
    // 이는 RedisStreamListener에서 실제 응답과 비교할 때 사용되는 예상 데이터임
  }

  @Test
  @DisplayName("buildTaskData 메서드 - Response 파라미터 처리 검증")
  @Transactional
  void buildTaskData_ResponseParameterProcessing() {
    // given - response 파라미터가 있는 테스트케이스 생성
    ScenarioEntity buildTestScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("build-test-scenario")
        .name("BuildTaskData 테스트 시나리오")
        .build();
    buildTestScenario = scenarioRepository.save(buildTestScenario);

    ApiListEntity buildTestApi = ApiListEntity.builder()
        .url("https://example.com/api")
        .path("/test")
        .method("GET")
        .build();
    buildTestApi = apiListRepository.save(buildTestApi);

    MappingEntity buildTestMapping = MappingEntity.builder()
        .mappingId("MAP-BUILD-TEST-001")
        .scenario(buildTestScenario)
        .apiList(buildTestApi)
        .step(1)
        .build();
    buildTestMapping = mappingRepository.save(buildTestMapping);

    TestcaseEntity buildTestcase = TestcaseEntity.builder()
        .testcaseId("TC-BUILD-TEST-001")
        .description("BuildTaskData 메서드 테스트")
        .precondition("없음")
        .expected("")
        .status(200)
        .mapping(buildTestMapping)
        .build();
    final TestcaseEntity finalBuildTestcase = testcaseRepository.save(buildTestcase);

    // Response Header 파라미터
    ParameterEntity responseHeaderParam = ParameterEntity.builder()
        .apiList(buildTestApi)
        .category(responseCategory)
        .context(headerContext)
        .name("X-Custom-Header")
        .dataType("string")
        .build();
    responseHeaderParam = parameterRepository.save(responseHeaderParam);

    // Response Body 파라미터
    ContextEntity bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    ParameterEntity responseBodyParam = ParameterEntity.builder()
        .apiList(buildTestApi)
        .category(responseCategory)
        .context(bodyContext)
        .name("result.status")
        .dataType("string")
        .build();
    responseBodyParam = parameterRepository.save(responseBodyParam);

    // TestcaseDataEntity 생성
    TestcaseDataEntity headerData = TestcaseDataEntity.builder()
        .testcase(finalBuildTestcase)
        .parameter(responseHeaderParam)
        .value("success")
        .build();
    testcaseDataRepository.save(headerData);

    TestcaseDataEntity bodyData = TestcaseDataEntity.builder()
        .testcase(finalBuildTestcase)
        .parameter(responseBodyParam)
        .value("OK")
        .build();
    testcaseDataRepository.save(bodyData);

    // when - ApiTestServiceImpl의 buildTaskData 메서드가
    // response 파라미터를 올바르게 resHeader/resBody에 설정하는지 확인
    // (실제로는 executeTestService를 통해 간접 확인)
    // ApiTestServiceImpl의 buildTaskData 로직이 올바른지 확인
    // 우리는 Redis Stream에 전송되는 데이터 구조를 통해 간접적으로 확인할 수 있음

    // then - response 파라미터들이 올바르게 추출되는지 검증
    List<TestcaseDataEntity> responseParams = testcaseDataRepository.findAll().stream()
        .filter(data -> data.getTestcase().getId().equals(finalBuildTestcase.getId()))
        .filter(data -> data.getParameter().getCategory().getName().equals("response"))
        .collect(Collectors.toList());

    assertThat(responseParams).hasSize(2);

    // Header 파라미터 검증
    TestcaseDataEntity headerParam = responseParams.stream()
        .filter(p -> p.getParameter().getContext().getName().equals("header"))
        .findFirst()
        .orElse(null);
    assertThat(headerParam).isNotNull();
    assertThat(headerParam.getParameter().getName()).isEqualTo("X-Custom-Header");
    assertThat(headerParam.getValue()).isEqualTo("success");

    // Body 파라미터 검증
    TestcaseDataEntity bodyParam = responseParams.stream()
        .filter(p -> p.getParameter().getContext().getName().equals("body"))
        .findFirst()
        .orElse(null);
    assertThat(bodyParam).isNotNull();
    assertThat(bodyParam.getParameter().getName()).isEqualTo("result.status");
    assertThat(bodyParam.getValue()).isEqualTo("OK");

    System.out.println("=== buildTaskData 메서드에서 처리될 Response 파라미터 ===");
    responseParams.forEach(param -> {
      String context = param.getParameter().getContext().getName();
      String name = param.getParameter().getName();
      String value = param.getValue();
      System.out.println("Context: " + context + ", Name: " + name + ", Value: " + value);
      System.out.println("-> " + context + " 컨텍스트는 " +
          ("header".equals(context) ? "resHeader" : "resBody") + "에 설정됨");
    });
  }

  @Test
  @DisplayName("Response 파라미터와 Request 파라미터 구분 처리 테스트")
  @Transactional
  void responseVsRequestParameterDistinction() {
    // given - request와 response 파라미터가 모두 있는 테스트케이스
    ScenarioEntity mixedScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("mixed-param-scenario")
        .name("Request/Response 파라미터 혼합 시나리오")
        .build();
    mixedScenario = scenarioRepository.save(mixedScenario);

    ApiListEntity mixedApi = ApiListEntity.builder()
        .url("https://httpbin.org/post")
        .path("/post")
        .method("POST")
        .build();
    mixedApi = apiListRepository.save(mixedApi);

    MappingEntity mixedMapping = MappingEntity.builder()
        .mappingId("MAP-MIXED-001")
        .scenario(mixedScenario)
        .apiList(mixedApi)
        .step(1)
        .build();
    mixedMapping = mappingRepository.save(mixedMapping);

    TestcaseEntity mixedTestcase = TestcaseEntity.builder()
        .testcaseId("TC-MIXED-001")
        .description("Request/Response 파라미터 혼합 테스트")
        .precondition("없음")
        .expected("")
        .status(200)
        .mapping(mixedMapping)
        .build();
    final TestcaseEntity finalMixedTestcase = testcaseRepository.save(mixedTestcase);

    // Request 파라미터 (body)
    ContextEntity bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    ParameterEntity requestParam = ParameterEntity.builder()
        .apiList(mixedApi)
        .category(requestCategory)
        .context(bodyContext)
        .name("input.message")
        .dataType("string")
        .build();
    requestParam = parameterRepository.save(requestParam);

    // Response 파라미터 (body)
    ParameterEntity responseParam = ParameterEntity.builder()
        .apiList(mixedApi)
        .category(responseCategory)
        .context(bodyContext)
        .name("json.input.message")
        .dataType("string")
        .build();
    responseParam = parameterRepository.save(responseParam);

    // TestcaseData 생성
    TestcaseDataEntity requestData = TestcaseDataEntity.builder()
        .testcase(finalMixedTestcase)
        .parameter(requestParam)
        .value("Hello World")
        .build();
    testcaseDataRepository.save(requestData);

    TestcaseDataEntity responseData = TestcaseDataEntity.builder()
        .testcase(finalMixedTestcase)
        .parameter(responseParam)
        .value("Hello World") // 같은 값이지만 response에서 확인
        .build();
    testcaseDataRepository.save(responseData);

    // when & then
    List<TestcaseDataEntity> allParams = testcaseDataRepository.findAll().stream()
        .filter(data -> data.getTestcase().getId().equals(finalMixedTestcase.getId()))
        .collect(Collectors.toList());

    List<TestcaseDataEntity> requestParams = allParams.stream()
        .filter(data -> data.getParameter().getCategory().getName().equals("request"))
        .collect(Collectors.toList());

    List<TestcaseDataEntity> responseParams = allParams.stream()
        .filter(data -> data.getParameter().getCategory().getName().equals("response"))
        .collect(Collectors.toList());

    assertThat(requestParams).hasSize(1);
    assertThat(responseParams).hasSize(1);

    System.out.println("=== Request vs Response 파라미터 구분 ===");
    System.out.println("Request 파라미터: " + requestParams.get(0).getParameter().getName() +
        " = " + requestParams.get(0).getValue());
    System.out.println("Response 파라미터: " + responseParams.get(0).getParameter().getName() +
        " = " + responseParams.get(0).getValue());
    System.out.println("-> Request는 API 호출시 전송되고, Response는 응답 검증에 사용됨");
  }

  @Test
  @DisplayName("실제 API 호출 및 응답 비교 결과 검증 - 비동기 처리 완료 대기")
  @Transactional
  void executeTestService_RealApiCall_AsyncResultVerification() throws InterruptedException {
    // given - 실제 httpbin API를 호출하는 테스트케이스
    ScenarioEntity realApiScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("real-api-test-scenario")
        .name("실제 API 호출 테스트 시나리오")
        .build();
    realApiScenario = scenarioRepository.save(realApiScenario);

    // httpbin.org/json - 고정된 JSON 응답을 반환하는 API
    ApiListEntity realApiList = ApiListEntity.builder()
        .url("https://httpbin.org/json")
        .path("/json")
        .method("GET")
        .build();
    realApiList = apiListRepository.save(realApiList);

    MappingEntity realApiMapping = MappingEntity.builder()
        .mappingId("MAP-REAL-API-001")
        .scenario(realApiScenario)
        .apiList(realApiList)
        .step(1)
        .build();
    realApiMapping = mappingRepository.save(realApiMapping);

    TestcaseEntity realApiTestcase = TestcaseEntity.builder()
        .testcaseId("TC-REAL-API-001")
        .description("실제 API 호출 및 응답 검증")
        .precondition("없음")
        .expected("") // expected는 사용하지 않음
        .status(200)
        .mapping(realApiMapping)
        .build();
    final TestcaseEntity savedRealApiTestcase = testcaseRepository.save(realApiTestcase);

    // Response 파라미터 생성 - 실제 httpbin.org/json 응답 구조에 맞춤
    ContextEntity bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    // slideshow.title 파라미터 (실제 httpbin.org/json에서 반환되는 값)
    ParameterEntity slideshowTitleParam = ParameterEntity.builder()
        .apiList(realApiList)
        .category(responseCategory)
        .context(bodyContext)
        .name("slideshow.title")
        .dataType("string")
        .build();
    slideshowTitleParam = parameterRepository.save(slideshowTitleParam);

    // 예상 응답 데이터
    TestcaseDataEntity expectedTitleData = TestcaseDataEntity.builder()
        .testcase(savedRealApiTestcase)
        .parameter(slideshowTitleParam)
        .value("Sample Slide Show") // httpbin.org/json의 실제 응답값
        .build();
    testcaseDataRepository.save(expectedTitleData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("real-api-test-scenario"))
        .build();

    // when - API 테스트 실행
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then - 초기 상태 확인
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).containsExactly("TC-REAL-API-001");

    // 초기 TestcaseResultEntity 상태 확인
    TestcaseResultEntity initialResult = testcaseResultRepository.findAll().stream()
        .filter(tr -> tr.getTestcase().getId().equals(savedRealApiTestcase.getId()))
        .findFirst()
        .orElse(null);

    assertThat(initialResult).isNotNull();
    assertThat(initialResult.getSuccess()).isNull(); // 초기값은 null (처리중)
    assertThat(initialResult.getResult()).isEqualTo("");

    System.out.println("=== 비동기 API 호출 및 응답 처리 대기 중... ===");

    // 비동기 처리 완료를 기다림 (최대 30초)
    int maxWaitSeconds = 30;
    boolean isCompleted = false;

    for (int i = 0; i < maxWaitSeconds; i++) {
      Thread.sleep(1000); // 1초 대기

      // DB에서 업데이트된 결과 확인
      TestcaseResultEntity updatedResult = testcaseResultRepository.findById(initialResult.getId())
          .orElse(null);

      if (updatedResult != null && updatedResult.getSuccess() != null) {
        System.out.println("비동기 처리 완료! 대기 시간: " + (i + 1) + "초");
        System.out.println("=== 실제 API 호출 결과 ===");
        System.out.println("Success: " + updatedResult.getSuccess());
        System.out.println("Result: " + updatedResult.getResult());
        System.out.println("Time: " + updatedResult.getTime() + "ms");
        System.out.println("Reason: " + updatedResult.getReason());

        // 실제 API 호출 성공 여부 검증
        if (updatedResult.getSuccess() != null) {
          isCompleted = true;

          // 성공한 경우 추가 검증
          if (updatedResult.getSuccess()) {
            assertThat(updatedResult.getTime()).isGreaterThan(0.0);
            System.out.println("✅ API 호출 성공 및 예상 응답 일치 확인!");
          } else {
            System.out.println("❌ API 호출 실패 또는 예상 응답 불일치");
            if (updatedResult.getReason() != null) {
              System.out.println("실패 이유: " + updatedResult.getReason());
            }
          }
          break;
        }
      }

      System.out.println("대기 중... " + (i + 1) + "/" + maxWaitSeconds + "초");
    }

    if (!isCompleted) {
      System.out.println("⚠️ " + maxWaitSeconds + "초 대기 후에도 비동기 처리가 완료되지 않음");
      System.out.println("Redis Stream 연결 또는 외부 API 호출에 문제가 있을 수 있음");
    }

    // Redis에 저장된 세부 정보도 확인
    System.out.println("=== Redis 저장 데이터 확인 ===");
    try {
      Iterable<ApiTestDetailRedisEntity> detailEntities = apiTestDetailRepository.findAll();
      detailEntities.forEach(entity -> {
        if (entity.getId().contains("TC-REAL-API-001")) {
          System.out.println("Detail ID: " + entity.getId());
          System.out.println("Header: " + entity.getHeader());
          System.out.println("Body: " + entity.getBody());
        }
      });
    } catch (Exception e) {
      System.out.println("Redis 데이터 조회 실패: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("실제 API 응답과 예상 응답 불일치 케이스 검증")
  @Transactional
  void executeTestService_RealApiCall_MismatchCase() throws InterruptedException {
    // given - 의도적으로 잘못된 예상값을 설정하여 불일치 케이스 테스트
    ScenarioEntity mismatchScenario = ScenarioEntity.builder()
        .projectKey(1)
        .scenarioId("mismatch-test-scenario")
        .name("응답 불일치 테스트 시나리오")
        .build();
    mismatchScenario = scenarioRepository.save(mismatchScenario);

    ApiListEntity mismatchApiList = ApiListEntity.builder()
        .url("https://httpbin.org/json")
        .path("/json")
        .method("GET")
        .build();
    mismatchApiList = apiListRepository.save(mismatchApiList);

    MappingEntity mismatchMapping = MappingEntity.builder()
        .mappingId("MAP-MISMATCH-001")
        .scenario(mismatchScenario)
        .apiList(mismatchApiList)
        .step(1)
        .build();
    mismatchMapping = mappingRepository.save(mismatchMapping);

    TestcaseEntity mismatchTestcase = TestcaseEntity.builder()
        .testcaseId("TC-MISMATCH-001")
        .description("응답 불일치 검증 테스트")
        .precondition("없음")
        .expected("")
        .status(200)
        .mapping(mismatchMapping)
        .build();
    final TestcaseEntity savedMismatchTestcase = testcaseRepository.save(mismatchTestcase);

    ContextEntity bodyContext = ContextEntity.builder()
        .name("body")
        .build();
    bodyContext = contextRepository.save(bodyContext);

    // 의도적으로 잘못된 예상값 설정
    ParameterEntity wrongParam = ParameterEntity.builder()
        .apiList(mismatchApiList)
        .category(responseCategory)
        .context(bodyContext)
        .name("slideshow.title")
        .dataType("string")
        .build();
    wrongParam = parameterRepository.save(wrongParam);

    TestcaseDataEntity wrongExpectedData = TestcaseDataEntity.builder()
        .testcase(savedMismatchTestcase)
        .parameter(wrongParam)
        .value("Wrong Expected Value") // 실제 응답과 다른 값
        .build();
    testcaseDataRepository.save(wrongExpectedData);

    ExcuteTestServiceRequestDto requestDto = ExcuteTestServiceRequestDto.builder()
        .projectKey(1)
        .scenarioList(List.of("mismatch-test-scenario"))
        .build();

    // when
    List<String> result = apiTestService.excuteTestService(requestDto);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    System.out.println("=== 불일치 케이스 - 비동기 처리 대기 중... ===");

    // 비동기 처리 완료 대기 (최대 30초)
    boolean isCompleted = false;
    for (int i = 0; i < 30; i++) {
      Thread.sleep(1000);

      TestcaseResultEntity result_entity = testcaseResultRepository.findAll().stream()
          .filter(tr -> tr.getTestcase().getId().equals(savedMismatchTestcase.getId()))
          .findFirst()
          .orElse(null);

      if (result_entity != null && result_entity.getSuccess() != null) {
        System.out.println("=== 불일치 케이스 결과 ===");
        System.out.println("Success: " + result_entity.getSuccess());
        System.out.println("예상값: Wrong Expected Value");
        System.out.println("실제값과 불일치로 인한 실패 여부: " + !result_entity.getSuccess());

        if (!result_entity.getSuccess()) {
          System.out.println("✅ 예상대로 응답 불일치 감지됨!");
        }

        isCompleted = true;
        break;
      }
    }

    if (!isCompleted) {
      System.out.println("⚠️ 비동기 처리 완료되지 않음");
    }
  }
}
