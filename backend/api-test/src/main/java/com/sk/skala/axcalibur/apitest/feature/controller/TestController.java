// package com.sk.skala.axcalibur.apitest.feature.controller;

// import com.fasterxml.uuid.impl.TimeBasedGenerator;
// import
// com.sk.skala.axcalibur.apitest.feature.dto.tmp.TokenRegistrationResponseDto;
// import com.sk.skala.axcalibur.apitest.feature.entity.ApiListEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.AvalonCookieEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.CategoryEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.ContextEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.MappingEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.ParameterEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseDataEntity;
// import com.sk.skala.axcalibur.apitest.feature.entity.TestcaseEntity;
// import com.sk.skala.axcalibur.apitest.feature.repository.ApiListRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.AvalonRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.CategoryRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.ContextRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.MappingRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.ParameterRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.ScenarioRepository;
// import
// com.sk.skala.axcalibur.apitest.feature.repository.TestcaseDataRepository;
// import com.sk.skala.axcalibur.apitest.feature.repository.TestcaseRepository;
// import com.sk.skala.axcalibur.apitest.feature.service.AvalonCookieService;
// import com.sk.skala.axcalibur.apitest.global.code.SuccessCode;
// import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import java.util.Map;

// import org.springframework.http.ResponseEntity;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// @Tag(name = "TokenController", description = "토큰 관리")
// @RestController
// @RequestMapping("/tmp")
// @RequiredArgsConstructor
// @Slf4j
// public class TestController {

// private final AvalonRepository repo;
// private final AvalonCookieService svc;
// private final TimeBasedGenerator uuid;
// private final ScenarioRepository scenarioRepository;
// private final ApiListRepository apiListRepository;
// private final MappingRepository mappingRepository;
// private final TestcaseRepository testcaseRepository;
// private final CategoryRepository categoryRepository;
// private final ContextRepository contextRepository;
// private final ParameterRepository parameterRepository;
// private final TestcaseDataRepository testcaseDataRepository;

// @Operation(summary = "프로젝트 키 기반 토큰 등록", description = "프로젝트 키를 입력받아 토큰을 생성하고
// Redis에 등록합니다.")
// @PostMapping("/token")
// public ResponseEntity<SuccessResponse<TokenRegistrationResponseDto>>
// registerToken(
// @Parameter(description = "토큰 등록 요청 데이터", required = true) @Valid
// @RequestParam Integer project) {
// log.info("토큰 등록 요청: projectKey={}", project);

// String token = uuid.generate().toString().replaceAll("-", "");
// AvalonCookieEntity entity = AvalonCookieEntity.of(token, project);
// repo.save(entity);

// TokenRegistrationResponseDto dto = TokenRegistrationResponseDto.builder()
// .token(token)
// .build();

// log.info("토큰 등록 성공: projectKey={}, tokenId={}", project, dto.token());

// return ResponseEntity
// .ok(new SuccessResponse<>(dto, SuccessCode.INSERT_SUCCESS,
// SuccessCode.INSERT_SUCCESS.getMessage()));
// }

// @Operation(summary = "토큰 유효성 검사", description = "등록된 토큰의 유효성을 검사합니다.")
// @GetMapping("/validate")
// public ResponseEntity<SuccessResponse<Map<String, Object>>> validateToken(
// @Parameter(description = "토큰", required = true) @RequestParam String token) {

// var entity = svc.findByToken(token);

// return ResponseEntity
// .ok(new SuccessResponse<>(Map.of("project", entity.getProjectKey()),
// SuccessCode.SELECT_SUCCESS, "유효한 토큰입니다."));
// }

// @Operation(summary = "데이터 입력", description = "입력 데이터를 처리합니다.")
// @PostMapping("/input")
// @Transactional
// public ResponseEntity<SuccessResponse<?>> inputData(@RequestParam String
// token) {
// log.info("데이터 입력 요청: token={}", token);

// Integer projectKey = svc.findByToken(token).getProjectKey();

// // 1. 시나리오 생성
// var testScenario = ScenarioEntity.builder()
// .projectKey(projectKey)
// .scenarioId("httpbin-test-scenario")
// .name("Httpbin API 테스트 시나리오")
// .build();
// testScenario = scenarioRepository.save(testScenario);

// // 2. API 목록 생성 (httpbin.org 엔드포인트)
// var testApiList = ApiListEntity.builder()
// .url("https://httpbin.org/get")
// .path("/get")
// .method("GET")
// .build();
// testApiList = apiListRepository.save(testApiList);

// // 3. 매핑 생성 (테스트케이스보다 먼저)
// var mapping1 = MappingEntity.builder()
// .mappingId("MAP-001")
// .scenario(testScenario)
// .apiList(testApiList)
// .step(1)
// .build();
// mapping1 = mappingRepository.save(mapping1);

// var mapping2 = MappingEntity.builder()
// .mappingId("MAP-002")
// .scenario(testScenario)
// .apiList(testApiList)
// .step(2)
// .build();
// mapping2 = mappingRepository.save(mapping2);

// // 4. 테스트케이스 생성
// var testcase1 = TestcaseEntity.builder()
// .testcaseId("TC-HTTPBIN-001")
// .description("Httpbin GET 요청 테스트 1")
// .precondition("없음")
// .expected("{\"args\": {}, \"headers\": {}, \"origin\": \"*\", \"url\":
// \"https://httpbin.org/get\"}")
// .status(2)
// .mapping(mapping1)
// .build();
// testcase1 = testcaseRepository.save(testcase1);

// var testcase2 = TestcaseEntity.builder()
// .testcaseId("TC-HTTPBIN-002")
// .description("Httpbin GET 요청 테스트 2")
// .precondition("쿼리 파라미터 포함")
// .expected(
// "{\"args\": {\"test\": \"value\"}, \"headers\": {}, \"origin\": \"*\",
// \"url\": \"https://httpbin.org/get?test=value\"}")
// .status(2)
// .mapping(mapping2)
// .build();
// testcase2 = testcaseRepository.save(testcase2);

// // 5. 카테고리 및 컨텍스트 생성
// var requestCategory = CategoryEntity.builder()
// .name("request")
// .build();
// requestCategory = categoryRepository.save(requestCategory);

// var responseCategory = CategoryEntity.builder()
// .name("response")
// .build();
// responseCategory = categoryRepository.save(responseCategory);

// var headerContext = ContextEntity.builder()
// .name("header")
// .build();
// headerContext = contextRepository.save(headerContext);

// var bodyContext = ContextEntity.builder()
// .name("query")
// .build();
// bodyContext = contextRepository.save(bodyContext);

// // 6. 파라미터 생성
// var headerParam = ParameterEntity.builder()
// .apiList(testApiList)
// .category(requestCategory)
// .context(headerContext)
// .name("User-Agent")
// .dataType("string")
// .build();
// headerParam = parameterRepository.save(headerParam);

// var bodyParam = ParameterEntity.builder()
// .apiList(testApiList)
// .category(requestCategory)
// .context(bodyContext)
// .name("test")
// .dataType("string")
// .build();
// bodyParam = parameterRepository.save(bodyParam);

// // 7. 테스트케이스 데이터 생성
// var headerData1 = TestcaseDataEntity.builder()
// .testcase(testcase1)
// .parameter(headerParam)
// .value("SpringBoot-IntegrationTest/1.0")
// .build();
// testcaseDataRepository.save(headerData1);

// var headerData2 = TestcaseDataEntity.builder()
// .testcase(testcase2)
// .parameter(headerParam)
// .value("SpringBoot-IntegrationTest/1.0")
// .build();
// testcaseDataRepository.save(headerData2);

// var bodyData2 = TestcaseDataEntity.builder()
// .testcase(testcase2)
// .parameter(bodyParam)
// .value("integration-test-value")
// .build();
// testcaseDataRepository.save(bodyData2);

// return ResponseEntity
// .ok(new SuccessResponse<>(null, SuccessCode.INSERT_SUCCESS, "Input data
// processed successfully."));
// }
// }
