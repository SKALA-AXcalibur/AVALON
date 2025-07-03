package com.sk.skala.axcalibur.apitest.feature.controller;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteApiTestRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteTestServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestCaseResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.request.GetTestResultServiceRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestCaseResultResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestResultResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.EmptyResponseDto;
import com.sk.skala.axcalibur.apitest.feature.service.ApiTestService;
import com.sk.skala.axcalibur.apitest.feature.service.AvalonCookieService;
import com.sk.skala.axcalibur.apitest.feature.service.ScenarioService;
import com.sk.skala.axcalibur.apitest.global.code.SuccessCode;
import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/v1")
public class ApiTestControllerImpl implements ApiTestController {

  private final ApiTestService svc;
  private final ScenarioService scene;
  private final AvalonCookieService cookie;

  /**
   * IF-AT-0001
   * API 테스트 실행
   * 시나리오 목록을 받아 API 테스트를 실행한다.
   * 
   * @param dto    API 테스트 요청 DTO
   * @param avalon 쿠키 값
   * @return 200 OK
   */
  @PostMapping("/run")
  @Override
  public ResponseEntity<SuccessResponse<EmptyResponseDto>> executeApiTest(
      @Valid @RequestBody ExcuteApiTestRequestDto dto,
      @CookieValue(name = "avalon") String avalon) {
    log.info("ApiTestControllerImpl.executeApiTest: called with avalon: {}", avalon);

    // validate cookie
    var entity = cookie.findByToken(avalon);
    Integer key = entity.getProjectKey();

    // excute svc
    var req = ExcuteTestServiceRequestDto.builder()
        .projectKey(key)
        .scenarioList(dto.scenarioList())
        .build();
    var list = svc.excuteTestService(req);

    // return
    var res = EmptyResponseDto.builder()
        .build();
    return ResponseEntity.ok(new SuccessResponse<>(res, SuccessCode.SELECT_SUCCESS, ""));
  }

  /**
   * IF-AT-0002
   * 시나리오별 테스트 결과 조회
   * 프로젝트의 전체 시나리오의 테스트 성공 여부를 조회한다.
   * 
   * @param avalon 쿠키 값
   * @param cursor 커서
   * @param size   페이지 크기
   * @return 200 OK
   */
  @GetMapping("/result")
  @Override
  public ResponseEntity<SuccessResponse<ApiTestResultResponseDto>> getApiTestResult(
      @Parameter(hidden = true) @CookieValue(name = "avalon") String avalon,
      @Parameter(required = false) @RequestParam(name = "cursor", required = false) String cursor,
      @Parameter(required = false) @RequestParam(name = "size", required = false) Integer size) {
    log.info("ApiTestControllerImpl.getApiTestResult: called with cursor: {}, size: {}", cursor, size);
    // validate cookie
    var entity = cookie.findByToken(avalon);
    Integer key = entity.getProjectKey();

    // excute svc
    var req = GetTestResultServiceRequestDto.builder()
        .projectKey(key)
        .cursor(cursor)
        .size(size)
        .build();

    // scenarioList
    var list = svc.getTestResultService(req);

    // return
    var res = ApiTestResultResponseDto.builder()
        .scenarioList(list)
        .build();
    return ResponseEntity.ok(new SuccessResponse<>(res, SuccessCode.SELECT_SUCCESS, ""));
  }

  /**
   * IF-AT-0003
   * 테스트케이스 별 테스트 결과 조회
   * 특정 시나리오의 테스트케이스 실행 결과를 조회한다.
   * 
   * @param avalon     쿠키 값
   * @param scenarioId 시나리오 ID
   * @param cursor     커서
   * @param size       페이지 크기
   * @return 200 OK
   */
  @GetMapping("/result/{scenarioId}")
  @Override
  public ResponseEntity<SuccessResponse<?>> getApiTestCaseResult(
      @Parameter(hidden = true) @CookieValue(name = "avalon") String avalon,
      @Parameter(required = true) @PathVariable("scenarioId") String scenarioId,
      @Parameter(required = false) @RequestParam(name = "cursor", required = false) String cursor,
      @Parameter(required = false) @RequestParam(name = "size", required = false) Integer size) {
    log.info("ApiTestControllerImpl.getApiTestCaseResult() called with scenarioId: {}, cursor: {}, size: {}",
        scenarioId, cursor, size);

    // validate cookie
    var entity = cookie.findByToken(avalon);
    Integer key = entity.getProjectKey();

    // excute svc
    var req = GetTestCaseResultServiceRequestDto.builder()
        .projectKey(key)
        .scenarioId(scenarioId)
        .cursor(cursor)
        .size(size)
        .build();
    var dto = scene.getScenarioService(scenarioId);
    // tcList
    var list = svc.getTestCaseResultService(req);

    // return
    var res = ApiTestCaseResultResponseDto.builder()
        .scenarioId(dto.scenarioId())
        .scenarioName(dto.scenarioName())
        .tcList(list)
        .build();
    return ResponseEntity.ok(new SuccessResponse<>(res, SuccessCode.SELECT_SUCCESS, ""));
  }

  @GetMapping("/root")
  @Override
  public ResponseEntity<SuccessResponse<Map<String, String>>> getRoot(
      @Parameter(required = false) @RequestParam(name = "key", required = false) String key,
      @Parameter(required = false) @RequestParam(name = "value", required = false) String value) {
    var res = new HashMap<String, String>();
    res.put("message", "API Test Controller is running");
    if (key != null && value != null && !key.isEmpty() && !value.isEmpty()) {
      res.put(key, value);
    }
    return ResponseEntity.ok(new SuccessResponse<>(res, SuccessCode.SELECT_SUCCESS, "API Test Controller is running"));
  }

}
