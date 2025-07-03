package com.sk.skala.axcalibur.feature.scenario.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 수정 서비스 구현체(IF-SN-0004)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioUpdateServiceImpl implements ScenarioUpdateService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioMappingService scenarioMappingService;

    @Override
    public SuccessResponse<Void> updateScenario(Integer projectKey, String scenarioId, ScenarioUpdateRequestDto requestDto) {
        // 시나리오 기본 정보 수정
        updateScenarioBasicInfo(projectKey, scenarioId, requestDto);
    
        generateMappingAndFlowchart(scenarioId);
        
        return SuccessResponse.<Void>builder()
            .data(null)
            .status(SuccessCode.UPDATE_SUCCESS)
            .message("시나리오 수정이 완료되었습니다.")
            .build();
    }
    
    @Transactional
    private void updateScenarioBasicInfo(Integer projectKey, String scenarioId, ScenarioUpdateRequestDto requestDto) {
        // 시나리오 ID로 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
            .orElseThrow(() -> new BusinessExceptionHandler("시나리오를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));
        
        // 해당 시나리오가 현재 프로젝트에 속하는지 확인
        if (!scenario.getProject().getId().equals(projectKey)) {
            throw new BusinessExceptionHandler("해당 프로젝트의 시나리오가 아닙니다.", ErrorCode.FORBIDDEN_ERROR);
        }
        
        // 시나리오 정보 업데이트
        ScenarioEntity updatedScenario = ScenarioEntity.builder()
            .id(scenario.getId())
            .scenarioId(scenario.getScenarioId())
            .name(requestDto.getName())
            .description(requestDto.getDescription())
            .validation(requestDto.getValidation())
            .flowChart(scenario.getFlowChart())
            .project(scenario.getProject())
            .build();
        
        scenarioRepository.save(updatedScenario);
        log.info("시나리오 {} 기본 정보 수정 완료", scenarioId);
    }
    
    private void generateMappingAndFlowchart(String scenarioId) {
        // 시나리오 수정 트랜잭션 완료 후 AI 서버 호출
        try {
            log.info("시나리오 수정 후 매핑 생성 시작 - 시나리오 ID: {}", scenarioId);
            
            // 1. 매핑 생성 및 저장
            scenarioMappingService.generateAndSaveMapping(scenarioId);
            log.info("시나리오 {} 매핑 생성 완료", scenarioId);
            
            // 2. 흐름도 생성 (FastAPI에서 자동 저장)
            scenarioMappingService.generateFlowchart(scenarioId);
            log.info("시나리오 {} 흐름도 생성 완료", scenarioId);
            
        } catch (Exception e) {
            log.error("시나리오 {} 수정 후 매핑/흐름도 생성 실패: {}", scenarioId, e.getMessage());
            // 매핑/흐름도 생성 실패 시에도 시나리오 수정은 성공으로 처리
            // 필요 시 별도 에러 처리 로직 추가 가능
        }
    }
}