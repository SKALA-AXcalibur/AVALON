package com.sk.skala.axcalibur.feature.scenario.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 추가 서비스 구현체(IF-SN-0003)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioCreateServiceImpl implements ScenarioCreateService {

    private final ScenarioRepository scenarioRepository;
    private final ProjectRepository projectRepository;
    private final ScenarioMappingService scenarioMappingService;

    @Override
    public ScenarioCreateResponseDto createScenario(Integer projectKey, ScenarioCreateRequestDto requestDto) {
        // 1. 시나리오 기본 정보 생성 
        String newScenarioId = createScenarioBasicInfo(projectKey, requestDto);
        
        // 2. 트랜잭션 완료 후 AI 서버 호출
        generateMappingAndFlowchart(newScenarioId);
        
        return ScenarioCreateResponseDto.builder()
            .id(newScenarioId)
            .build();
    }
    
    @Transactional
    private String createScenarioBasicInfo(Integer projectKey, ScenarioCreateRequestDto requestDto) {
        // 프로젝트 존재 여부 확인
        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("프로젝트를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));

        try {
            // 프로젝트 내 기존 시나리오 id 목록을 조회
            int maxNo = scenarioRepository.findMaxScenarioNoByProjectKey(projectKey); // 기존 시나리오 id 중 최대 번호
            int newNo = maxNo + 1;
            String newScenarioId = String.format("scenario-%03d", newNo); // 새로운 시나리오 ID 생성

            // 시나리오 엔티티 생성 및 저장
            ScenarioEntity scenarioEntity = ScenarioEntity.builder()
                .scenarioId(newScenarioId)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .validation(requestDto.getValidation())
                .project(project)
                .build();

            scenarioRepository.save(scenarioEntity);
            
            return newScenarioId;

        } catch (DataIntegrityViolationException e) {
            // 시나리오 ID 중복 등 데이터 무결성 위반
            log.error("시나리오 생성 중 데이터 무결성 위반 - 프로젝트: {}, 에러: {}", projectKey, e.getMessage());
            throw new BusinessExceptionHandler("시나리오 ID가 중복되었습니다. 다시 시도해주세요.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    private void generateMappingAndFlowchart(String scenarioId) {
        // 시나리오 생성 트랜잭션 완료 후 AI 서버 호출
        try {
            // 1. 매핑 생성 및 저장
            scenarioMappingService.generateAndSaveMapping(scenarioId);
            
            // 2. 흐름도 생성 (FastAPI에서 자동 저장)
            scenarioMappingService.generateFlowchart(scenarioId);
            
        } catch (Exception e) {
            log.error("시나리오 {} 추가 후 매핑/흐름도 생성 실패: {}", scenarioId, e.getMessage());
        }
    }
}