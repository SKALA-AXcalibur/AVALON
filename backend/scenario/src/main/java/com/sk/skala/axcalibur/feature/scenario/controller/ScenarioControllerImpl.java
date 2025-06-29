package com.sk.skala.axcalibur.feature.scenario.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.dto.ProjectContext;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioListRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDeleteResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDetailResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioListDto;
import com.sk.skala.axcalibur.feature.scenario.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioCreateService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioDeleteService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioDetailService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioListService;
import com.sk.skala.axcalibur.feature.scenario.service.ScenarioUpdateService;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.response.SuccessResponse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 컨트롤러 구현체
 * - 시나리오 목록 조회(IF-SN-0009)
 * - 시나리오 생성(IF-SN-0003)
 * - 시나리오 수정(IF-SN-0004)
 * - 시나리오 삭제(IF-SN-0007)
 * - 시나리오 상세 조회(IF-SN-0008)
 * - 쿠키로부터 project ID를 불러와 시나리오 관련 작업을 진행
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ScenarioControllerImpl implements ScenarioController {

    private final ScenarioListService scenarioListService;
    private final ScenarioCreateService scenarioCreateService;
    private final ScenarioUpdateService scenarioUpdateService;
    private final ScenarioDeleteService scenarioDeleteService;
    private final ScenarioDetailService scenarioDetailService;
    private final ProjectIdResolverService projectIdResolverService;

    @Override
    @GetMapping("/scenario/v1/project")
    public ResponseEntity<SuccessResponse<ScenarioListDto>> getScenarioList(
        @CookieValue("avalon") String key,
        ScenarioListRequestDto requestDto) {
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // 시나리오 목록 조회
        ScenarioListDto scenarioList = scenarioListService.getScenarioList(projectKey, requestDto.getOffset(), requestDto.getQuery());
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .headers(headers)
            .body(SuccessResponse.<ScenarioListDto>builder()
                .data(scenarioList)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }

    @Override
    @PostMapping("/scenario/v1")
    public ResponseEntity<SuccessResponse<ScenarioCreateResponseDto>> createScenario(
        @CookieValue("avalon") String key,
        @RequestBody ScenarioCreateRequestDto requestDto) {
        
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // 시나리오 생성
        ScenarioCreateResponseDto result = scenarioCreateService.createScenario(projectKey, requestDto);
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
            .status(SuccessCode.INSERT_SUCCESS.getStatus())
            .headers(headers)
            .body(SuccessResponse.<ScenarioCreateResponseDto>builder()
                .data(result)
                .status(SuccessCode.INSERT_SUCCESS)
                .message(SuccessCode.INSERT_SUCCESS.getMessage())
                .build());
    }

    @Override
    @PutMapping("/scenario/v1/{scenarioId}")
    public ResponseEntity<SuccessResponse<Void>> updateScenario(
        @CookieValue("avalon") String key,
        @PathVariable("scenarioId") String scenarioId,
        @RequestBody ScenarioUpdateRequestDto requestDto) {
        
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // 시나리오 수정
        scenarioUpdateService.updateScenario(projectKey, scenarioId, requestDto);
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환 (데이터 없음)
        return ResponseEntity
            .status(SuccessCode.UPDATE_SUCCESS.getStatus())
            .headers(headers)
            .body(SuccessResponse.<Void>builder()
                .data(null)
                .status(SuccessCode.UPDATE_SUCCESS)
                .message(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build());
    }

    @Override
    @DeleteMapping("/scenario/v1/scenario/{scenarioId}")
    public ResponseEntity<SuccessResponse<ScenarioDeleteResponseDto>> deleteScenario(
        @CookieValue("avalon") String key,
        @PathVariable("scenarioId") String scenarioId) {
        
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // 시나리오 삭제
        ScenarioDeleteResponseDto result = scenarioDeleteService.deleteScenario(projectKey, scenarioId);
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
            .status(SuccessCode.DELETE_SUCCESS.getStatus())
            .headers(headers)
            .body(SuccessResponse.<ScenarioDeleteResponseDto>builder()
                .data(result)
                .status(SuccessCode.DELETE_SUCCESS)
                .message(SuccessCode.DELETE_SUCCESS.getMessage())
                .build());
    }

    @Override
    @GetMapping("/scenario/v1/scenario/{scenarioId}")
    public ResponseEntity<SuccessResponse<ScenarioDetailResponseDto>> getScenarioDetail(
        @CookieValue("avalon") String key,
        @PathVariable("scenarioId") String scenarioId) {
        
        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);
        Integer projectKey = projectContext.getKey();

        // 시나리오 상세 조회
        ScenarioDetailResponseDto result = scenarioDetailService.getScenarioDetail(projectKey, scenarioId);
        
        // 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        
        // 성공 응답 반환
        return ResponseEntity
            .status(SuccessCode.SELECT_SUCCESS.getStatus())
            .headers(headers)
            .body(SuccessResponse.<ScenarioDetailResponseDto>builder()
                .data(result)
                .status(SuccessCode.SELECT_SUCCESS)
                .message(SuccessCode.SELECT_SUCCESS.getMessage())
                .build());
    }
} 