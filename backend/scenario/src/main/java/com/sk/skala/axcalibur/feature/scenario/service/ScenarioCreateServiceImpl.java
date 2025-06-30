package com.sk.skala.axcalibur.feature.scenario.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioCreateRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCreateResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 추가 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioCreateServiceImpl implements ScenarioCreateService {

    private final ScenarioRepository scenarioRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ScenarioCreateResponseDto createScenario(Integer projectKey, ScenarioCreateRequestDto requestDto) {
        
        // 프로젝트 존재 여부 확인
        ProjectEntity project = projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("프로젝트를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));

        int maxRetry = 3; // 최대 재시도 횟수
        for(int attempt = 0; attempt < maxRetry; attempt++) {
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

                ScenarioEntity savedScenario = scenarioRepository.save(scenarioEntity);

                return ScenarioCreateResponseDto.builder()
                    .id(savedScenario.getScenarioId())
                    .build();

            } catch (DataIntegrityViolationException e) {
                // 동시성 충돌(유니크 인덱스 위반) 시 재시도
                log.warn("동시성 충돌로 인한 재시도({}/{}) - 프로젝트: {}", attempt+1, maxRetry, projectKey);
                if (attempt == maxRetry - 1) {
                    throw new BusinessExceptionHandler("시나리오 ID 중복, 재시도 실패", ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        throw new BusinessExceptionHandler("시나리오 생성에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
    }
}