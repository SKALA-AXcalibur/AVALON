package com.sk.skala.axcalibur.apitest.feature.controller;


import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;

public interface IApiTestController {
  // API 테스트 실행
  @Operation(summary = "API 테스트 실행", description = "JSON 형태의 시나리오에 대해 테스트 도구를 활용해 API 테스트를 실행한다.")
  ResponseEntity<SuccessResponse<?>> executeApiTest(String scenarioId, @Parameter(hidden = true) String avalon);
  // API 테스트 실행 결과 조회
  @Operation(summary = "API 테스트 실행 결과 조회", description = "API 테스트 실행 결과 데이터를 조회하여 실행 결과를 반환한다.")
  ResponseEntity<SuccessResponse<?>> getApiTestResult(String testExecutionId, @Parameter(hidden = true) String avalon);


}
