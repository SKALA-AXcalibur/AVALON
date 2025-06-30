package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcUpdateRequest;

/**
 * 테스트케이스 관리 커맨드 서비스 인터페이스
 * 테스트케이스에 대한 쓰기 작업(등록, 수정, 삭제)을 정의합니다.
 * - 테스트케이스 정보 및 데이터 항목의 수정
 * - 테스트케이스 삭제
 */

public interface TcCommandService {
    void deleteTestcase(String tcId, Integer projectId);
    void updateTestcase(String tcId, Integer projectId, TcUpdateRequest request);
    String addTestcase(String scenarioId, String apiId, TcUpdateRequest request);
}
