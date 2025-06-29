package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDetailResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 상세 조회 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioDetailServiceImpl implements ScenarioDetailService {

    private final ScenarioRepository scenarioRepository;

    @Override
    @Transactional(readOnly = true)
    public ScenarioDetailResponseDto getScenarioDetail(Integer projectKey, String scenarioId) {
        try {
            // 시나리오 ID로 시나리오 조회
            Optional<ScenarioEntity> scenarioOpt = scenarioRepository.findByScenarioId(scenarioId);
            
            if (scenarioOpt.isEmpty()) {
                throw new BusinessExceptionHandler("시나리오를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR);
            }
            
            ScenarioEntity scenario = scenarioOpt.get();
            
            // 해당 시나리오가 현재 프로젝트에 속하는지 확인
            if (!scenario.getProject().getId().equals(projectKey)) {
                throw new BusinessExceptionHandler("해당 프로젝트의 시나리오가 아닙니다.", ErrorCode.FORBIDDEN_ERROR);
            }
            
            log.info("시나리오 상세 조회 완료 - ID: {}, 프로젝트: {}", scenarioId, projectKey);
            
            // DTO 변환 및 반환
            return ScenarioDetailResponseDto.builder()
                .id(scenario.getScenarioId())
                .name(scenario.getName())
                .graph(scenario.getFlowChart()) // flowChart -> graph로 매핑
                .description(scenario.getDescription())
                .validation(scenario.getValidation())
                .build();
            
        } catch (BusinessExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("시나리오 상세 조회 실패 - 시나리오ID: {}, 프로젝트: {}", scenarioId, projectKey, e);
            throw new BusinessExceptionHandler("시나리오 상세 조회 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
} 