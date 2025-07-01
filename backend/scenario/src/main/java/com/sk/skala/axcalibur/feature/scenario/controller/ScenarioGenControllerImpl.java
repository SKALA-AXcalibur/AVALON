package com.sk.skala.axcalibur.feature.scenario.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.ProjectContext;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioGenRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListResponse;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioResponseDto;
import com.sk.skala.axcalibur.feature.scenario.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioGenService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 생성 요청 인터페이스
 * '시나리오 생성 요청(IF-SN-0001)'
 * - 쿠키로부터 project ID를 불러와 시나리오 생성 요청을 진행
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ScenarioGenControllerImpl implements ScenarioGenController {

    private final ScenarioGenService scenarioGenService;
    private final ScenarioGenClient scenarioGenClient;
    private final ProjectIdResolverService projectIdResolverService;

    @Override
    public ResponseEntity<ScenarioGenResponseDto> generateScenario(
        @CookieValue("avalon") String key) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // DB에서 프로젝트 관련 정보 수집하여 FastAPI 요청 데이터 준비
        ScenarioGenRequestDto requestDto = scenarioGenService.prepareRequestData(projectKey);
        
        // FastAPI로 시나리오 생성 요청 전송하고 응답 받기 (동기식)
        ScenarioResponseDto response = scenarioGenClient.sendInfoAndGetResponse(requestDto);
        
        // FastAPI 응답의 시나리오 리스트를 DB에 저장
        List<ScenarioEntity> savedEntities = scenarioGenService.parseAndSaveScenarios(response.getScenarioList(), projectKey);

        // ScenarioEntity를 ScenarioListResponse로 변환
        List<ScenarioListResponse> scenarioListResponse = savedEntities.stream()
            .map(entity -> ScenarioListResponse.builder()
                .scenarioId(entity.getScenarioId())
                .name(entity.getName())
                .build())
            .collect(Collectors.toList());

        // 최종 응답 DTO 구성
        ScenarioGenResponseDto responseDto = ScenarioGenResponseDto.builder()
            .scenarioList(scenarioListResponse)
            .total(scenarioListResponse.size())
            .build();
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
            .status(SuccessCode.INSERT_SUCCESS.getStatus())
            .headers(headers)
            .body(responseDto);
    }
}

