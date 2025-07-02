package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowScenarioItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioFlowResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCUResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 플로우차트 생성 서비스 구현
 * - 시나리오와 API 정보 수집 (매핑표 기준)
 * - AI 서비스 호출하여 플로우차트 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioFlowServiceImpl implements ScenarioFlowService {

    private final ScenarioGenClient scenarioGenClient;
    private final ScenarioRepository scenarioRepository;
    private final MappingRepository mappingRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ScenarioFlowRequestDto prepareAllScenariosFlowData(Integer projectKey) {
        // 프로젝트 존재 여부 확인
        projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트의 모든 시나리오 조회 및 변환
        List<ScenarioEntity> scenarios = scenarioRepository.findByProject_Id(projectKey);
        List<ScenarioFlowScenarioItem> scenarioItems = convertScenariosToFlowItems(scenarios);
        
        return ScenarioFlowRequestDto.builder()
            .scenarioList(scenarioItems)
            .build();
    }

    @Override
    public ScenarioFlowRequestDto prepareSingleScenarioFlowData(ScenarioCUResponseDto result) {
        // 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(result.getId())
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 시나리오입니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        // 단일 시나리오를 리스트로 변환
        List<ScenarioEntity> scenarios = Arrays.asList(scenario);
        List<ScenarioFlowScenarioItem> scenarioItems = convertScenariosToFlowItems(scenarios);
        
        return ScenarioFlowRequestDto.builder()
            .scenarioList(scenarioItems)
            .build();
    }

    @Override
    public ScenarioFlowResponseDto generateFlowchartForAllScenarios(Integer projectKey) {
        try {
            // 1. 모든 시나리오 데이터 준비
            ScenarioFlowRequestDto requestDto = prepareAllScenariosFlowData(projectKey);
            
            // 2. AI 서비스 호출
            ScenarioFlowResponseDto response = scenarioGenClient.generateFlowChart(requestDto);
            
            log.info("모든 시나리오 플로우차트 생성 완료. 프로젝트: {}, 시나리오 수: {}", projectKey, requestDto.getScenarioList().size());
            return response;
            
        } catch (Exception e) {
            log.error("모든 시나리오 플로우차트 생성 실패. 프로젝트: {}, 에러: {}", projectKey, e.getMessage());
            throw new BusinessExceptionHandler("플로우차트 생성 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ScenarioFlowResponseDto generateFlowchartForSingleScenario(ScenarioCUResponseDto result) {
        try {
            // 1. 개별 시나리오 데이터 준비
            ScenarioFlowRequestDto requestDto = prepareSingleScenarioFlowData(result);
            
            // 2. AI 서비스 호출
            ScenarioFlowResponseDto response = scenarioGenClient.generateFlowChart(requestDto);
            
            log.info("개별 시나리오 플로우차트 생성 완료");
            return response;
            
        } catch (Exception e) {
            log.error("개별 시나리오 플로우차트 생성 실패. 시나리오 ID: {}, 에러: {}", result.getId(), e.getMessage());
            throw new BusinessExceptionHandler("플로우차트 생성 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시나리오 엔티티 리스트를 ScenarioFlowScenarioItem 리스트로 변환 (매핑표 기반 API 포함)
     * @param scenarios 시나리오 엔티티 리스트
     * @return ScenarioFlowScenarioItem 리스트
     */
    private List<ScenarioFlowScenarioItem> convertScenariosToFlowItems(List<ScenarioEntity> scenarios) {
        return scenarios.stream()
            .map(scenario -> {
                // 시나리오별 매핑된 API 조회
                List<MappingEntity> mappings = mappingRepository.findByScenarioKey_Id(scenario.getId());
                List<ScenarioFlowApiItem> apiItems = mappings.stream()
                    .map(mapping -> ScenarioFlowApiItem.builder()
                        .id(mapping.getApiListKey().getApiListId())
                        .name(mapping.getApiListKey().getName())
                        .description(mapping.getApiListKey().getDescription())
                        .build())
                    .collect(Collectors.toList());

                return ScenarioFlowScenarioItem.builder()
                    .id(scenario.getScenarioId())
                    .description(scenario.getDescription())
                    .apiList(apiItems)
                    .build();
            })
            .collect(Collectors.toList());
    }
} 