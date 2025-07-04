package com.sk.skala.axcalibur.spec.feature.report.service;

import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestCaseReportResponseDto;
import com.sk.skala.axcalibur.spec.feature.report.dto.response.TestScenarioReportResponseDto;

import java.io.IOException;

/**
 * 리포트 관리 서비스 인터페이스
 * 테스트 시나리오 및 테스트케이스 리포트 생성 및 다운로드 기능 제공
 */
public interface ReportService {
    
    /**
     * 테스트시나리오 리포트 다운로드
     * @param avalon 사용자 토큰
     * @return 테스트시나리오 리포트 응답 DTO
     * @throws IOException 파일 처리 중 오류가 발생한 경우
     */
    TestScenarioReportResponseDto downloadTestScenarioReport(String avalon) throws IOException;

    /**
     * 테스트케이스 리포트 다운로드
     * @param scenarioId 시나리오 ID
     * @param avalon 사용자 토큰
     * @return 테스트케이스 리포트 응답 DTO
     * @throws IOException 파일 처리 중 오류가 발생한 경우
     */
    TestCaseReportResponseDto downloadTestCaseReport(String scenarioId, String avalon) throws IOException;
}