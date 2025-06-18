package com.sk.skala.axcalibur.spec.feature.spec.controller;

import lombok.RequiredArgsConstructor;


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

import com.sk.skala.axcalibur.spec.feature.spec.dto.ProjectContext;
import com.sk.skala.axcalibur.spec.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.spec.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.spec.feature.spec.service.ProjectIdResolverService;
import com.sk.skala.axcalibur.spec.feature.spec.service.SpecAnalyzeService;
import com.sk.skala.axcalibur.spec.global.code.ErrorCode;
import com.sk.skala.axcalibur.spec.global.code.SuccessCode;
import com.sk.skala.axcalibur.spec.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

/**
 * 명세서 분석 컨트롤러
 * - '명세서 분석(IF-SP-0004)'
 * @param key 인증용 Cookie 문자열
 * @return 업로드 결과 반환
 * - 요청 데이터는 유효성 검사를 거쳐 서비스에 전달
 * - 예외 발생 시 global.exception.GlobalExceptionHandler 에서 일괄 처리 
 */
@RestController
@RequestMapping("/spec/v1/analyze")
@RequiredArgsConstructor
public class ParseControllerImpl implements ParseController {
    

    // 서비스 주입
    private final SpecAnalyzeService specAnalyzeService;
    private final ProjectIdResolverService projectIdResolverService;
    private final ProjectRepository projectRepository;

    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<List<Object>>> analyzeSpec(
        @CookieValue("avalon") String key ) {

        // Redis에서 projectId 가져오기 (예외 발생 시 Global handler에서 처리)
        ProjectContext projectContext = projectIdResolverService.resolveProjectId(key);

        ProjectEntity project = projectRepository.findById(projectContext.getKey())
        .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        // 서비스 호출
        specAnalyzeService.analyze(project);

        // 응답시간 헤더에 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("responseTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // 정상 처리 응답(data는 null)
        return ResponseEntity
        .status(SuccessCode.INSERT_SUCCESS.getStatus())
        .headers(headers)
        .body(SuccessResponse.<List<Object>>builder()
            .data(Collections.emptyList())
            .status(SuccessCode.INSERT_SUCCESS)
            .message("요청 성공")
            .build());
        }
    }