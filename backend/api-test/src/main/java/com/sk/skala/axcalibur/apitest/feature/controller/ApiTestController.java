package com.sk.skala.axcalibur.apitest.feature.controller;

import com.sk.skala.axcalibur.apitest.feature.dto.request.ExcuteApiTestRequestDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ApiTestResultResponseDto;
import com.sk.skala.axcalibur.apitest.feature.dto.response.ExcuteApiTestResponseDto;
import com.sk.skala.axcalibur.apitest.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "ApiTestController", description = "API 테스트")
public interface ApiTestController {

    // API 테스트 실행
    @Operation(summary = "API 테스트 실행(IF-AT-0001)", description = "JSON 형태의 시나리오에 대해 테스트 도구를 활용해 API 테스트를 실행한다.")
    ResponseEntity<SuccessResponse<ExcuteApiTestResponseDto>> executeApiTest(
            ExcuteApiTestRequestDto dto,
            @Parameter(hidden = true) String avalon);

    // 시나리오별 테스트 결과 조회
    @Operation(summary = "시나리오별 테스트 결과 조회(IF-AT-0002)", description = "프로젝트의 전체 시나리오의 테스트 성공 여부를 조회한다.")
    ResponseEntity<SuccessResponse<ApiTestResultResponseDto>> getApiTestResult(
            @Parameter(hidden = true) String avalon,
            @Parameter(required = false) String cursor,
            @Parameter(required = false) Integer size);

    // 테스트케이스 별 테스트 결과 조회
    @Operation(summary = "테스트케이스 별 테스트 결과 조회(IF-AT-0003)", description = "특정 시나리오의 테스트케이스 실행 결과를 조회한다.")
    ResponseEntity<SuccessResponse<?>> getApiTestCaseResult(
            @Parameter(hidden = true) String avalon,
            @Parameter(required = true) String scenarioId,
            @Parameter(required = false) String cursor,
            @Parameter(required = false) Integer size);

}
