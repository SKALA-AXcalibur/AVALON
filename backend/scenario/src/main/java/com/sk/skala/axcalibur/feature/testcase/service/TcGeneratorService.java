package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TestcaseGenerationResponse;

/**
 * TC 생성 단계의 서비스
 * 조합된 request 객체를 fastAPI에 보내고,
 * 생성 이후 받은 응답을 DB에 저장합니다.
 */
public interface TcGeneratorService {
    List<TestcaseGenerationResponse> callFastApi(TcRequestPayload payload);
    void saveTestcases(List<TestcaseGenerationResponse> response);
}
