package com.sk.skala.axcalibur.spec.feature.report.controller;

import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestScenarioReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.service.ReportService;
import com.sk.skala.axcalibur.spec.global.code.SuccessCode;
import com.sk.skala.axcalibur.spec.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 프로젝트 정보 관리 컨트롤러
 * 
 * 프로젝트 정보 저장, 조회, 삭제, 생성, 쿠키 삭제 기능 제공
 * 
 * 
 */

@RestController
@RequestMapping("/report/v1")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    /**
     * IF-RG-0001: 테스트시나리오 리포트 다운로드
     */
    @GetMapping("/scenario")
    public ResponseEntity<Resource> downloadTestScenarioReport(String avalon) throws IOException {
        log.info("[테스트시나리오 리포트 다운로드] 요청. avalon: {}", avalon);
        TestScenarioReportResponseDto response = reportService.downloadTestScenarioReport(avalon);

        ByteArrayResource resource = new ByteArrayResource(response.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(resource);
    }

    /**
     * IF-RG-0002: 테스트케이스 리포트 다운로드
     */
    @GetMapping("/testcase/{scenarioId}")
    public ResponseEntity<Resource> downloadTestCaseReport(
            @PathVariable("scenarioId") String scenarioId,
            @CookieValue(name = "avalon") String avalon) {

        log.info("[테스트케이스 리포트 다운로드] 요청. scenarioId: {}", scenarioId);
        TestScenarioReportResponseDto response = reportService.downloadTestScenarioReport(scenarioId, avalon);

        ByteArrayResource resource = new ByteArrayResource(response.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(resource);
    }
}