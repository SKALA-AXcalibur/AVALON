package com.sk.skala.axcalibur.spec.feature.report.service;

import com.sk.skala.axcalibur.spec.feature.report.dto.response.ReportResponseDto;

/**
 * 리포트 관리 서비스 인터페이스
 * 테스트 시나리오 및 테스트케이스 리포트 생성 및 다운로드 기능 제공
 */
public interface ReportService {
    
    /**
     * 테스트시나리오 리포트 다운로드
     * @param avalon 사용자 토큰
     * @return 테스트시나리오 리포트 응답 DTO
     */
    ReportResponseDto downloadTestScenarioReport(String avalon);

    /**
     * 테스트케이스 리포트 다운로드
     * @param scenarioId 시나리오 ID
     * @param avalon 사용자 토큰
     * @return 테스트케이스 리포트 응답 DTO
     */
    ReportResponseDto downloadTestCaseReport(String scenarioId, String avalon);
}