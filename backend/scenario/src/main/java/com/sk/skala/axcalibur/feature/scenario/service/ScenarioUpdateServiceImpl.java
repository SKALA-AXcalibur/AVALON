package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 수정 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioUpdateServiceImpl implements ScenarioUpdateService {

    private final ScenarioRepository scenarioRepository;

    @Override
    @Transactional
    public void updateScenario(Integer projectKey, String scenarioId, ScenarioUpdateRequestDto requestDto) {
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
            
            // 시나리오 정보 업데이트 (Builder 패턴으로 새 객체 생성)
            ScenarioEntity updatedScenario = ScenarioEntity.builder()
                .id(scenario.getId())
                .scenarioId(scenario.getScenarioId())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .validation(requestDto.getValidation())
                .project(scenario.getProject())
                .build();
            
            scenarioRepository.save(updatedScenario);
            
        } catch (BusinessExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("시나리오 수정 실패 - 시나리오ID: {}, 프로젝트: {}, 요청: {}", scenarioId, projectKey, requestDto, e);
            throw new BusinessExceptionHandler("시나리오 수정 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
} 