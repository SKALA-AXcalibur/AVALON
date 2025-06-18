//package com.sk.skala.axcalibur.apitest.feature.service;
//
//import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
//import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
//import com.sk.skala.axcalibur.apitest.feature.dto.response.ScenarioResponseDto;
//import com.sk.skala.axcalibur.apitest.feature.dto.response.TestcaseInfoResponseDto;
//import com.sk.skala.axcalibur.apitest.feature.entity.ApiListEntity;
//import com.sk.skala.axcalibur.apitest.feature.entity.MappingEntity;
//import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
//import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
//import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseResultEntity;
//import com.sk.skala.axcalibur.apitest.feature.repository.ApiListRepository;
//import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
//import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
//import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
//import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseResultRepository;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
//class ApiTestServiceTest {
//    @Autowired
//    ApiTestService apiTestService;
//    @Autowired
//    ScenarioRepository scenarioRepository;
//    @Autowired
//    MappingRepository mappingRepository;
//    @Autowired
//    ApiListRepository apiListRepository;
//    @Autowired
//    TestcaseRepository testcaseRepository;
//    @Autowired
//    TestcaseResultRepository testcaseResultRepository;
//
//    // 테스트 데이터 입력 메서드
//    void insertTestData() {
//        // 1. API 엔티티 생성
//        ApiListEntity api = ApiListEntity.builder()
//                .url("/test/url")
//                .path("/test/path")
//                .method("GET")
//                .build();
//        api = apiListRepository.save(api);
//
//        // 2. 시나리오 생성
//        ScenarioEntity scenario = ScenarioEntity.builder()
//                .projectKey(1)
//                .scenarioId("S1")
//                .name("시나리오1")
//                .build();
//        scenario = scenarioRepository.save(scenario);
//
//        // 3. 매핑 생성
//        MappingEntity mapping = MappingEntity.builder()
//                .mappingId("M1")
//                .step(1)
//                .scenario(scenario)
//                .apiList(api)
//                .build();
//        mapping = mappingRepository.save(mapping);
//
//        // 4. 테스트케이스 2개 생성
//        TestcaseEntity tc1 = TestcaseEntity.builder()
//                .testcaseId("TC1")
//                .description("desc1")
//                .expected("ok")
//                .mapping(mapping)
//                .build();
//        TestcaseEntity tc2 = TestcaseEntity.builder()
//                .testcaseId("TC2")
//                .description("desc2")
//                .expected("fail")
//                .mapping(mapping)
//                .build();
//        testcaseRepository.save(tc1);
//        testcaseRepository.save(tc2);
//
//        // 5. 테스트케이스 결과 2개 생성 (각 테스트케이스별 1개)
//        TestcaseResultEntity result1 = TestcaseResultEntity.builder()
//                .testcase(tc1)
//                .result("ok")
//                .success(true)
//                .time(LocalDateTime.now())
//                .build();
//        TestcaseResultEntity result2 = TestcaseResultEntity.builder()
//                .testcase(tc2)
//                .result("fail")
//                .success(false)
//                .time(LocalDateTime.now())
//                .build();
//        testcaseResultRepository.save(result1);
//        testcaseResultRepository.save(result2);
//    }
//
//    @BeforeEach
//    void setUp() {
//        // 테스트 데이터 클린업
//        testcaseResultRepository.deleteAll();
//        testcaseRepository.deleteAll();
//        mappingRepository.deleteAll();
//        scenarioRepository.deleteAll();
//        apiListRepository.deleteAll();
//        insertTestData();
//    }
//
//    @Test
//    @DisplayName("getTestResultService - 정상 케이스: 여러 시나리오 결과 반환")
//    void getTestResultService_success_multipleScenarios() {
//        // given
//        GetTestResultServiceRequestDto req = GetTestResultServiceRequestDto.builder()
//                .projectKey(1)
//                .cursor(null)
//                .size(2)
//                .build();
//        // when
//        List<ScenarioResponseDto> result = apiTestService.getTestResultService(req);
//        // then
//        System.out.println("[getTestResultService 결과]");
//        result.forEach(r -> System.out.println(r));
//        System.out.println("[getTestResultService 예상 결과 예시]");
//        System.out.println("ScenarioResponseDto(scenarioId=S1, scenarioName=시나리오1, isSuccess=Y)");
//        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
//        assertThat(result.getFirst().scenarioId()).isNotBlank();
//    }
//
//    @Test
//    @DisplayName("getTestResultService - 비정상 케이스: 존재하지 않는 프로젝트")
//    void getTestResultService_fail_invalidProjectKey() {
//        // given
//        GetTestResultServiceRequestDto req = GetTestResultServiceRequestDto.builder()
//                .projectKey(-1)
//                .cursor(null)
//                .size(2)
//                .build();
//        // when
//        List<ScenarioResponseDto> result = apiTestService.getTestResultService(req);
//        System.out.println("[getTestResultService (비정상) 결과]");
//        result.forEach(r -> System.out.println(r));
//        System.out.println("[getTestResultService (비정상) 예상 결과: 빈 리스트 []]");
//        // then
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    @DisplayName("getTestCaseResultService - 정상 케이스: 여러 테스트케이스 결과 반환")
//    void getTestCaseResultService_success_multipleTestcases() {
//        // given
//        GetTestCaseResultServiceRequestDto req = GetTestCaseResultServiceRequestDto.builder()
//                .projectKey(1)
//                .scenarioId("S1")
//                .cursor(null)
//                .size(2)
//                .build();
//        // when
//        List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(req);
//        System.out.println("[getTestCaseResultService 결과]");
//        result.forEach(r -> System.out.println(r));
//        System.out.println("[getTestCaseResultService 예상 결과 예시]");
//        System.out.println("TestcaseInfoResponseDto(tcId=TC1, description=desc1, expectedResult=ok, isSuccess=Y, excutedTime=...)");
//        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
//        assertThat(result.getFirst().tcId()).isNotBlank();
//    }
//
//    @Test
//    @DisplayName("getTestCaseResultService - 비정상 케이스: 존재하지 않는 시나리오")
//    void getTestCaseResultService_fail_invalidScenarioId() {
//        // given
//        GetTestCaseResultServiceRequestDto req = GetTestCaseResultServiceRequestDto.builder()
//                .projectKey(1)
//                .scenarioId("NOT_EXIST")
//                .cursor(null)
//                .size(2)
//                .build();
//        // when
//        List<TestcaseInfoResponseDto> result = apiTestService.getTestCaseResultService(req);
//        System.out.println("[getTestCaseResultService (비정상) 결과]");
//        result.forEach(r -> System.out.println(r));
//        System.out.println("[getTestCaseResultService (비정상) 예상 결과: 빈 리스트 []]");
//        // then
//        assertThat(result).isEmpty();
//    }
//}