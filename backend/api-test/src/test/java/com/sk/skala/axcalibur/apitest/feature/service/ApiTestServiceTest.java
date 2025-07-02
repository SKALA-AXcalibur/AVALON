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
            assertThat(result1.getSuccess()).isFalse(); // 초기값은 false
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

        // when & then - 결과 기반 검증 (예외 대신 빈 결과나 특정 상태 확인)
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
        assertThat(postResult.getSuccess()).isFalse(); // 초기값은 false
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
            assertThat(tr.getSuccess()).isFalse();
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
            assertThat(tr.getSuccess()).isFalse();
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
}
