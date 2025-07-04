package com.sk.skala.axcalibur.spec.feature.report.controller;

import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestCaseReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestScenarioReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 리포트 생성 및 다운로드 컨트롤러
 * 
 * 테스트 시나리오 및 테스트케이스 리포트 다운로드 기능을 제공합니다.
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
    public ResponseEntity<Resource> downloadTestScenarioReport(@CookieValue(name = "avalon") String avalon) {
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
        TestCaseReportResponseDto response = reportService.downloadTestCaseReport(scenarioId, avalon);

        ByteArrayResource resource = new ByteArrayResource(response.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(resource);
    }
}