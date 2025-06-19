package com.sk.skala.axcalibur.feature.testcase.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.testcase.dto.request.DbTableDto;
import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseGenerationResponse;
import com.sk.skala.axcalibur.feature.testcase.entity.ScenarioEntity;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.testcase.service.TcGeneratorService;
import com.sk.skala.axcalibur.feature.testcase.service.TcPayloadService;

import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 테스트케이스 생성 요청 인터페이스
 * '테스트케이스 생성 요청(IF-TC-0001)'을 실제 구현합니다.
 * - 쿠키로부터 Project key를 전달받아 해당 프로젝트에 매핑된 시나리오와 테이블 설계서 정보를 불러옵니다.
 * - 시나리오에 매핑된 매핑표와 각 API 정보를 조회합니다.
 * - 시나리오 단위로 필요한 정보를 조합하여 생성 서버에 요청합니다.
 * - TC 생성 정보를 받아와 DB에 저장합니다.
 */
@RestController
@RequestMapping("/tc/v1")
@RequiredArgsConstructor
@Slf4j
public class TcGeneratorControllerImpl implements TcGeneratorContoller {
    private final ProjectIdResolverService projectIdResolverService;
    private final TcPayloadService tcPayloadService;
    private final TcGeneratorService tcGeneratorService;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<List<Object>>> generateTestCases(@CookieValue("avalon") String key) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        Integer projectId = projectIdResolverService.resolveProjectId(key);

        // project ID로 필요 DB 조회 -> fastAPI 측에 필요한 생성 정보 + 생성 요청 전달
        // 시나리오 리스트 조회
        List<ScenarioEntity> scenarios = tcPayloadService.getScenarios(projectId);

        // ERD 테이블 정보 수집
        List<DbTableDto> dbList = tcPayloadService.getDbTableList(projectId);

        // 시나리오별 테스트케이스 생성 로직
        for (ScenarioEntity scenario : scenarios) {
            // payload 조립
            TcRequestPayload payload = tcPayloadService.buildPayload(scenario, dbList);
            
            // FastAPI 호출
            TestcaseGenerationResponse response = tcGeneratorService.callFastApi(payload, scenario);
            
            // 결과 저장
            tcGeneratorService.saveTestcases(response);
        }
        
        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<List<Object>>builder()
            .data(Collections.emptyList())  // 빈 리스트 반환
            .status(SuccessCode.INSERT_SUCCESS)
            .message(SuccessCode.INSERT_SUCCESS.getMessage())
            .build());
    }
}
