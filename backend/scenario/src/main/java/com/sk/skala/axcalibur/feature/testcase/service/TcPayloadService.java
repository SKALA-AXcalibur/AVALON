package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.List;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcGenerationRequest;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;

/**
 * TC 생성 이전 단계의 서비스
 * project ID로부터 DB를 조회하여 TC 생성에 필요한 정보를 조합합니다.
 */
public interface TcPayloadService {
    List<ScenarioEntity> getScenarios(Integer projectId);
    TcGenerationRequest buildPayload(ScenarioEntity scenario);
}
