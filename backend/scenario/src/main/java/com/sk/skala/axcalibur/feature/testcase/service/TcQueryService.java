package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.response.TcDetailResponse;

/**
 * 테스트케이스 조회 서비스 인터페이스
 * - TC 상세 정보 및 연관 파라미터 조회
 */
public interface TcQueryService {
    TcDetailResponse getTestcaseDetail(String testcaseId);
}
