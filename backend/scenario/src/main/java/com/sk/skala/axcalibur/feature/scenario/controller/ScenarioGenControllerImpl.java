package com.sk.skala.axcalibur.feature.scenario.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioGenService;
import com.sk.skala.axcalibur.feature.testcase.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 생성 요청 인터페이스
 * '시나리오 생성 요청(IF-SN-0001)'
 * - 쿠키로부터 project ID를 불러와 시나리오 생성 요청을 진행
 */
@Slf4j
@RestController
@RequestMapping("/scenario/v1/create")
@RequiredArgsConstructor
public class ScenarioGenControllerImpl implements ScenarioGenController {

    private final ScenarioGenService scenarioGenService;
    private final ScenarioGenClient scenarioGenClient;
    private final ProjectIdResolverService projectIdResolverService;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<ScenarioGenResponseDto>> generateScenario(
        @CookieValue("avalon") String key) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        Integer projectKey = projectIdResolverService.resolveProjectId(key);

        // DB에서 프로젝트 관련 정보 수집하여 FastAPI 요청 데이터 준비
        ScenarioGenRequestDto requestDto = scenarioGenService.prepareRequestData(projectKey);
        
        // FastAPI로 시나리오 생성 요청 전송하고 응답 받기
        ScenarioGenResponseDto fastApiResponse = scenarioGenClient.sendInfoAndGetResponse(requestDto);

        // FastAPI 응답의 시나리오 리스트를 DB에 저장
        List<ScenarioListResponse> savedScenarios = scenarioGenService.parseAndSaveScenarios(
            fastApiResponse.getScenarioList(), projectKey
        );

        // 저장된 시나리오에서 흐름도 생성 API 호출
        try {
            scenarioGenService.generateAndUpdateFlowChart(savedScenarios, projectKey);
            log.info("시나리오 흐름도 생성 완료");
        } catch (Exception e) {
            log.error("시나리오 흐름도 생성 실패", e);
        }

        // 최종 응답 DTO 구성
        ScenarioGenResponseDto responseDto = ScenarioGenResponseDto.builder()
            .scenarioList(savedScenarios)
            .total(savedScenarios.size())
            .build();
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<ScenarioGenResponseDto>builder()
            .data(responseDto)  // 실제 응답 DTO 반환
            .status(SuccessCode.INSERT_SUCCESS)
            .message(SuccessCode.INSERT_SUCCESS.getMessage())
            .build());
    }
}

