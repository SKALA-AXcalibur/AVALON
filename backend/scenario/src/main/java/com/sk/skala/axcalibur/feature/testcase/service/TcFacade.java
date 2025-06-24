package com.sk.skala.axcalibur.feature.testcase.service;

/**
 * Controller 단순화용 Service 객체 정의
 * payload 생성 - FastAPI 호출 - 결과 저장 - 로깅 후의 결과를 반환합니다
 */
public interface TcFacade {
    void generateAllTestcases(Integer projectId);
}
