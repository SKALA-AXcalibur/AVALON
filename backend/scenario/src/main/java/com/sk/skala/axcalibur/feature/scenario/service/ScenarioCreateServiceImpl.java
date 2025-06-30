package com.sk.skala.axcalibur.feature.scenario.service;

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
        try {
            // 프로젝트 존재 여부 확인
            ProjectEntity project = projectRepository.findById(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("프로젝트를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));

            // 새로운 시나리오 ID 생성
            String newScenarioId = generateNewScenarioId();

            // 시나리오 엔티티 생성 및 저장
            ScenarioEntity scenarioEntity = ScenarioEntity.builder()
                .scenarioId(newScenarioId)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .validation(requestDto.getValidation())
                .project(project)
                .build();

            ScenarioEntity savedScenario = scenarioRepository.save(scenarioEntity);

            log.info("새로운 시나리오 생성 완료 - ID: {}, 프로젝트: {}", newScenarioId, projectKey);

            return ScenarioCreateResponseDto.builder()
                .id(savedScenario.getScenarioId())
                .build();

        } catch (BusinessExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            log.error("시나리오 생성 실패 - 프로젝트: {}, 요청: {}", projectKey, requestDto, e);
            throw new BusinessExceptionHandler("시나리오 생성 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String generateNewScenarioId() {
        // 새로운 시나리오 ID 생성
        // DB에서 현재 최대 시나리오 ID를 조회해서 +1
        String maxId = scenarioRepository.findMaxScenarioId();
        int nextNum = 1;
        if (maxId != null && maxId.startsWith("scenario-")) {
            try {
                nextNum = Integer.parseInt(maxId.substring(9)) + 1;
            } catch (NumberFormatException e) {
                log.warn("시나리오 ID 숫자 변환 실패: {}", maxId);
                // 무시하고 1로 둠
            }
        }
        return String.format("scenario-%03d", nextNum);
    }
} 