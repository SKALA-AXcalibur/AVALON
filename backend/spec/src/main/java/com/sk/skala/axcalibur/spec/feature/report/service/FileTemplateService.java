package com.sk.skala.axcalibur.spec.feature.report.service;

import com.sk.skala.axcalibur.spec.feature.report.entity.ScenarioEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseDataEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestCaseEntity;
import com.sk.skala.axcalibur.spec.feature.report.entity.TestcaseResultEntity;

import java.io.IOException;
import java.util.List;

/**
 * 파일 템플릿 처리 서비스 인터페이스
 * 엑셀 템플릿 기반 리포트 생성 기능 제공
 */
public interface FileTemplateService {
    
    /**
     * 시나리오 리포트 생성
     * @param scenarios 시나리오 목록
     * @param businessFunction 업무 기능명
     * @return 엑셀 파일 바이트 배열
     * @throws IOException 파일 처리 중 오류가 발생한 경우
     */
    byte[] generateScenarioReport(List<ScenarioEntity> scenarios, String businessFunction) throws IOException;

    /**
     * 테스트케이스 리포트 생성
     * @param testCases 테스트케이스 목록
     * @param testCaseData 테스트케이스 데이터 목록
     * @param testCaseResults 테스트케이스 결과 목록
     * @param businessFunction 업무 기능명
     * @return 엑셀 파일 바이트 배열
     * @throws IOException 파일 처리 중 오류가 발생한 경우
     */
    byte[] generateTestCaseReport(List<TestCaseEntity> testCases, List<TestCaseDataEntity> testCaseData, 
                                 List<TestcaseResultEntity> testCaseResults, String businessFunction) throws IOException;
}