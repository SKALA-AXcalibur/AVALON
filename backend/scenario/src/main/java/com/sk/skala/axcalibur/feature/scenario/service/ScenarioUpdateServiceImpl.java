package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioUpdateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioUpdateDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

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

    @Override
    @Transactional
    public ScenarioUpdateDto updateScenario(Integer projectKey, String scenarioId, ScenarioUpdateRequestDto requestDto) {
  
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
        
        ScenarioEntity savedScenario = scenarioRepository.save(updatedScenario);
        
        return ScenarioUpdateDto.builder()
            .scenarioId(savedScenario.getScenarioId())
            .build();
    }
}