package com.sk.skala.axcalibur.feature.testcase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.testcase.dto.request.TcGenerationRequest;
import com.sk.skala.axcalibur.feature.testcase.dto.response.TcGenerationResponse;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller 단순화용 Service 객체의 실제 구현부
 * payload 생성 - FastAPI 호출 - 결과 저장 - 로깅 후의 결과를 반환합니다
 * - 시나리오에 매핑된 매핑표와 각 API 정보를 조회합니다.
 * - 시나리오 단위로 필요한 정보를 조합하여 생성 서버에 요청합니다.
 * - TC 생성 정보를 받아와 DB에 저장합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TcFacadeImpl implements TcFacade {

    private final TcPayloadService tcPayloadService;
    private final TcGeneratorService tcGeneratorService;

    @Transactional
    public void generateAllTestcases(Integer projectId) {
        List<ScenarioEntity> scenarios = tcPayloadService.getScenarios(projectId);
        List<String> failedScenarioIds = new ArrayList<>();
        if (scenarios.isEmpty()) {
            log.warn("생성할 시나리오가 없습니다 (projectId: {})", projectId);
            return;
        }

        for (ScenarioEntity scenario : scenarios) {
            boolean success = false;

            for (int i = 0; i < 3; i++) {
                try {
                    TcGenerationRequest payload = tcPayloadService.buildPayload(scenario);
                    log.info("시나리오 {} 테스트케이스 생성 요청 (projectId: {})", scenario.getScenarioId(), projectId);
                    TcGenerationResponse response = tcGeneratorService.callFastApi(payload, scenario);
                    log.info("시나리오 {} 테스트케이스 생성 완료 (projectId: {})", scenario.getScenarioId(), projectId);
                    tcGeneratorService.saveTestcases(response);
                    log.info("시나리오 {} 테스트케이스 저장 완료 (projectId: {})", scenario.getScenarioId(), projectId);
                    success = true;
                    break;  // 재시도 루프 탈출
                } catch (BusinessExceptionHandler e) {
                    log.warn("시도 {} 실패 - 시나리오 {}: {}", i + 1, scenario.getScenarioId(), e.getMessage());
                    if (e.getErrorCode() == ErrorCode.INTERNAL_SERVER_ERROR) throw e; // FastAPI 서버 장애 발생시 전체 중단
                }
            }

            if (!success) failedScenarioIds.add(scenario.getScenarioId());
            log.info("시나리오 {} 테스트케이스 완료 (projectId: {})", scenario.getScenarioId(), projectId);
        }

        if (failedScenarioIds.size() == scenarios.size()) {
            throw new BusinessExceptionHandler("모든 시나리오 테스트케이스 생성 실패", ErrorCode.NOT_VALID_ERROR);
        }

        if (!failedScenarioIds.isEmpty()) { // 재시도 후에도 저장 실패 시나리오가 남아있는 경우
            throw new BusinessExceptionHandler("일부 시나리오 실패", ErrorCode.NOT_VALID_ERROR);
        }

        log.info("모든 시나리오 테스트케이스 생성 완료 (projectId: {})", projectId);
    }
}
