package com.sk.skala.axcalibur.feature.testcase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.request.ApiParamDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.SupportQueryService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트케이스 추가 관련 인터페이스(IF-TC-0007 ~ IF-TC-0010)
 * - 시나리오 ID로부터 API 목록을 조회하고, 선택한 API의 파라미터 정보를 활용해 TC를 추가하는 파트를 구현합니다.
 * - API 조회(IF-TC-0007)
 * - API 선택(IF-TC-0008)
 * - TC 추가(IF-TC-0010)
 */
@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TcSupportControllerImpl implements TcSupportController {
    private final ProjectIdResolverService projectIdResolverService;
    private final SupportQueryService supportQueryService;
    // IF-TC-0007: API 조회
    @Override
    @GetMapping("/api/{scenarioId}")
    public ResponseEntity<SuccessResponse<List<ApiListResponse>>> getApiListByScenario(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        List<ApiListResponse> response = supportQueryService.getApiListByScenario(scenarioId);

        return ResponseEntity.ok(
            SuccessResponse.<List<ApiListResponse>>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build()
        );
    }

    // IF-TC-0008: API 선택
    @Override
    @GetMapping("/api/{scenarioId}/{apiId}")
    public ResponseEntity<SuccessResponse<List<ApiParamDto>>> getParamListByApi(
        @PathVariable String scenarioId,
        @PathVariable String apiId,
        @CookieValue("avalon") String key
    ) {

    }

    @Override
    @PostMapping("/api/{scenarioId}/{apiId}")
    public ResponseEntity<SuccessResponse<Void>> createTestcase(
        @PathVariable String scenarioId,
        @PathVariable String apiId,
        @CookieValue("avalon") String key,
        @RequestBody TcUpdateRequest request
    ) {

    }
}
