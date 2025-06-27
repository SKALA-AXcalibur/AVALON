package com.sk.skala.axcalibur.feature.testcase.service;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcRequestPayload;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcGenerationResponse;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

/**
 * TC 생성 단계의 서비스
 * 조합된 request 객체를 fastAPI에 보내고,
 * 생성 이후 받은 응답을 DB에 저장합니다.
 */
public interface TcGeneratorService {
    TcGenerationResponse callFastApi(TcRequestPayload payload, ScenarioEntity scenario);
    void saveTestcases(TcGenerationResponse response);
}
