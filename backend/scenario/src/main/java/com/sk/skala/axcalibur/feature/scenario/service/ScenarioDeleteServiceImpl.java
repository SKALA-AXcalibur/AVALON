package com.sk.skala.axcalibur.feature.scenario.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioDeleteResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.code.SuccessCode;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 삭제 서비스 구현체(IF-SN-0007)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioDeleteServiceImpl implements ScenarioDeleteService {

    private final ScenarioRepository scenarioRepository;
    private final MappingRepository mappingRepository;

    @Override
    @Transactional
    public SuccessResponse<ScenarioDeleteResponseDto> deleteScenario(Integer projectKey, String scenarioId) {

        // 시나리오 ID로 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
        .orElseThrow(() -> new BusinessExceptionHandler("시나리오를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));
        
        // 해당 시나리오가 현재 프로젝트에 속하는지 확인
        if (!scenario.getProject().getId().equals(projectKey)) {
            throw new BusinessExceptionHandler("해당 프로젝트의 시나리오가 아닙니다.", ErrorCode.FORBIDDEN_ERROR);
        }
        
        // 먼저 관련 매핑 데이터 삭제 (외래키 제약조건 해결)
        mappingRepository.deleteByScenarioKey_Id(scenario.getId());
        
        // 시나리오 삭제
        scenarioRepository.delete(scenario);
        
        log.info("시나리오 삭제 완료 - ID: {}, 프로젝트: {}", scenarioId, projectKey);
        
        ScenarioDeleteResponseDto responseDto = ScenarioDeleteResponseDto.builder()
            .id(scenarioId)
            .build();
            
        return SuccessResponse.<ScenarioDeleteResponseDto>builder()
            .data(responseDto)
            .status(SuccessCode.DELETE_SUCCESS)
            .message("시나리오 삭제가 완료되었습니다.")
            .build();
    }
}