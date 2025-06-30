package com.sk.skala.axcalibur.feature.testcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiListResponse;
import com.sk.skala.axcalibur.feature.testcase.dto.response.ApiParamListResponse;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.SupportQueryService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트케이스 추가 전 보조 정보 조회 인터페이스의 실제 구현부(IF-TC-0007, IF-TC-0008)
 * - 테스트케이스를 수동으로 추가하기 전, 시나리오 기반으로 필요한 정보를 조회합니다.
 * - API 조회(IF-TC-0007)
 * - API 선택(IF-TC-0008)
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
    public ResponseEntity<SuccessResponse<ApiListResponse>> getApiListByScenario(
        @PathVariable String scenarioId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        ApiListResponse response = supportQueryService.getApiListByScenario(scenarioId);

        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .body(SuccessResponse.<ApiListResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }

    // IF-TC-0008: API 선택
    @Override
    @GetMapping("/api/{scenarioId}/{apiId}")
    public ResponseEntity<SuccessResponse<ApiParamListResponse>> getParamListByApi(
        @PathVariable String scenarioId,
        @PathVariable String apiId,
        @CookieValue("avalon") String key
    ) {
        // Redis에서 cookie 키 인증 (예외 발생 시 Global handler에서 처리)
        projectIdResolverService.resolveProjectId(key);

        // 조회된 API 목록에서 API 선택하면 해당 API에 속한 파라미터 목록 반환
        ApiParamListResponse response = supportQueryService.getParamsByApiId(apiId);

        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .body(SuccessResponse.<ApiParamListResponse>builder()
                .data(response)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }
}
