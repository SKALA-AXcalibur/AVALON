// ReportService.java
package com.sk.skala.axcalibur.spec.feature.report.service;

import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestScenarioReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.repository.ScenarioRepository;
import com.sk.skala.axcalibur.spec.global.repository.AvalonCookieRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ScenarioRepository scenarioRepository;
    private final AvalonCookieRepository avalonCookieRepository;
    private final FileTemplateService fileTemplateService;

    /**
     * 테스트시나리오 리포트 다운로드
     */
    public TestScenarioReportResponseDto downloadTestScenarioReport(String avalon) {
        // 1. 토큰 검증
        // 2. 시나리오 데이터 조회
        // 3. 엑셀 리포트 생성
        byte[] fileData = fileTemplateService.generateScenarioReport();
        
        return TestScenarioReportResponseDto.builder()
                .avalon(avalon)
                .fileName("scenario_report.xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .fileData(fileData)
                .build();
    }

    /**
     * 테스트케이스 리포트 다운로드
     */
    public TestScenarioReportResponseDto downloadTestScenarioReport(String scenarioId, String avalon) {
        // 1. 토큰 검증
        // 2. 시나리오 조회
        // 3. 테스트케이스 데이터 조회
        // 4. 엑셀 리포트 생성
        byte[] fileData = fileTemplateService.generateTestCaseReport();
        
        return TestScenarioReportResponseDto.builder()
                .avalon(avalon)
                .fileName("testcase_report.xlsx")
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .fileData(fileData)
                .build();
    }
}