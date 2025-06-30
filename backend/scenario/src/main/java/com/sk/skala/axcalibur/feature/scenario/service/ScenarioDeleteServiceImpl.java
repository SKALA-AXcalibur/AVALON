package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDeleteResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 삭제 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioDeleteServiceImpl implements ScenarioDeleteService {

    private final ScenarioRepository scenarioRepository;
    private final MappingRepository mappingRepository;

    @Override
    @Transactional
    public ScenarioDeleteResponseDto deleteScenario(Integer projectKey, String scenarioId) {
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
            
            // 먼저 관련 매핑 데이터 삭제 (외래키 제약조건 해결)
            mappingRepository.deleteByScenarioKey_Id(scenario.getId());
            
            // 시나리오 삭제
            scenarioRepository.delete(scenario);
            
            log.info("시나리오 삭제 완료 - ID: {}, 프로젝트: {}", scenarioId, projectKey);
            
            return ScenarioDeleteResponseDto.builder()
                .id(scenarioId)
                .build();
            
        } catch (BusinessExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("시나리오 삭제 실패 - 시나리오ID: {}, 프로젝트: {}", scenarioId, projectKey, e);
            throw new BusinessExceptionHandler("시나리오 삭제 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
} 