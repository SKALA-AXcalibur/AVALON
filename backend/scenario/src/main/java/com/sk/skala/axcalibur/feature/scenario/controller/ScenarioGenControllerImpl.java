package com.sk.skala.axcalibur.feature.scenario.controller;

import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioGenResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.ProjectContext;
import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.feature.scenario.entity.ProjectEntity;
import com.sk.skala.axcalibur.feature.scenario.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

@RestController
@RequestMapping("/api/scenario/v1/create")
@RequiredArgsConstructor
public class ScenarioGenControllerImpl implements ScenarioGenController {

    // 서비스 주입
    private final ProjectIdResolverService projectIdResolverService;
    private final ProjectRepository projectRepository;
    private final ScenarioGenClient scenarioGenClient;


    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<ScenarioGenResponseDto>> generateScenario(
        @CookieValue("avalon") String key ) {

        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);

        ProjectEntity project = projectRepository.findById(projectContext.getKey())
        .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        // 서비스 호출

        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<ScenarioGenResponseDto>builder()
            .data(new ScenarioGenResponseDto())
            .status(SuccessCode.INSERT_SUCCESS)
            .message("시나리오 생성 요청 완료")
            .build());
        }
    }
