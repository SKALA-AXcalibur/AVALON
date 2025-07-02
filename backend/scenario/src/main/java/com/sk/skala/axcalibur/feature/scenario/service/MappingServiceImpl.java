package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.request.MappingRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.MappingApiItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.MappingScenarioItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.MappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioCUResponseDto;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ProjectEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ApiListRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.repository.ProjectRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 매핑 생성 서비스 구현
 * - 시나리오 정보 수집
 * - AI 서비스 호출하여 매핑 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MappingServiceImpl implements MappingService {

    private final ScenarioGenClient scenarioGenClient;
    private final ScenarioRepository scenarioRepository;
    private final ProjectRepository projectRepository;
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;

    @Override
    public MappingRequestDto prepareAllMappingData(Integer projectKey) {
        // 프로젝트 존재 여부 확인
        projectRepository.findById(projectKey)
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트의 모든 시나리오 조회
        List<ScenarioEntity> scenarios = scenarioRepository.findByProject_Id(projectKey);
        List<MappingScenarioItem> scenarioItems = scenarios.stream()
            .map(scenario -> MappingScenarioItem.builder()
                .scenarioId(scenario.getScenarioId())
                .title(scenario.getName())
                .description(scenario.getDescription())
                .validation(scenario.getValidation())
                .build())
            .collect(Collectors.toList());

        // 프로젝트의 모든 API 조회
        List<ApiListEntity> apiList = apiListRepository.findByProject_Id(projectKey);
        List<MappingApiItem> apiItems = apiList.stream()
            .map(api -> MappingApiItem.builder()
                .apiName(api.getName())
                .url(api.getUrl())
                .method(api.getMethod())
                .description(api.getDescription())
                .build())
            .collect(Collectors.toList());

        return MappingRequestDto.builder()
            .scenarioList(scenarioItems)
            .apiList(apiItems)
            .build();
    }

    @Override
    public MappingRequestDto prepareSingleMappingData(ScenarioCUResponseDto result) {
        // 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(result.getId())
            .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 시나리오입니다.", ErrorCode.INTERNAL_SERVER_ERROR));

        // 특정 시나리오 하나만 MappingScenarioItem으로 변환
        List<MappingScenarioItem> scenarioItems = Arrays.asList(scenario)
            .stream()
            .map(s -> MappingScenarioItem.builder()
                .scenarioId(s.getScenarioId())
                .title(s.getName())
                .description(s.getDescription())
                .validation(s.getValidation())
                .build())
            .collect(Collectors.toList());

        // 프로젝트의 모든 API 조회 (매핑을 위해 전체 API 목록 필요)
        List<ApiListEntity> apiList = apiListRepository.findByProject_Id(scenario.getProject().getId());
        List<MappingApiItem> apiItems = apiList.stream()
            .map(api -> MappingApiItem.builder()
                .apiName(api.getName())
                .url(api.getUrl())
                .method(api.getMethod())
                .description(api.getDescription())
                .build())
            .collect(Collectors.toList());

        return MappingRequestDto.builder()
            .scenarioList(scenarioItems)
            .apiList(apiItems)
            .build();
    }

    @Override
    @Transactional
    public List<MappingEntity> generateMappingForAllScenarios(Integer projectKey) {
        try {
            // 1. 프로젝트 조회 및 검증
            ProjectEntity project = projectRepository.findById(projectKey)
                .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 프로젝트입니다.", ErrorCode.PROJECT_NOT_FOUND));

            // 2. 모든 시나리오 매핑 데이터 준비
            MappingRequestDto requestDto = prepareAllMappingData(projectKey);
            
            // 3. AI 서비스 호출
            MappingResponseDto response = scenarioGenClient.MappingResponse(requestDto);
            
            // 4. 기존 매핑 ID 중 최대 번호 조회 (시나리오와 동일한 패턴)
            int maxNo = mappingRepository.findMaxMappingNoByProjectKey(projectKey);
            
            // 5. 엔티티 리스트 생성
            List<MappingEntity> entitiesToSave = new ArrayList<>();
            
            // response.getMappingList()에서 각 매핑 아이템을 처리
            for (int i = 0; i < response.getMappingList().size(); i++) {
                var mappingItem = response.getMappingList().get(i);
                String newMappingId = String.format("mapping-%03d", maxNo + 1 + i);
                
                // 시나리오 조회
                ScenarioEntity scenario = scenarioRepository.findByScenarioId(mappingItem.getScenarioId())
                    .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 시나리오입니다.", ErrorCode.INTERNAL_SERVER_ERROR));
                
                // API 조회
                ApiListEntity apiList = apiListRepository.findByNameAndProject_Id(mappingItem.getApiName(), projectKey)
                    .orElseThrow(() -> new BusinessExceptionHandler("존재하지 않는 API입니다.", ErrorCode.INTERNAL_SERVER_ERROR));
                
                // MappingEntity 생성
                MappingEntity entity = MappingEntity.builder()
                    .mappingId(newMappingId)
                    .step(mappingItem.getStep())
                    .scenarioKey(scenario)
                    .apiListKey(apiList)
                    .build();
                
                entitiesToSave.add(entity);
            }
            
            // 6. 일괄 저장
            List<MappingEntity> savedEntities = mappingRepository.saveAll(entitiesToSave);
            
            log.info("모든 시나리오 매핑 생성 및 저장 완료. 프로젝트: {}, 저장된 매핑 수: {}", 
                projectKey, savedEntities.size());
            
            return savedEntities;
            
        } catch (Exception e) {
            log.error("모든 시나리오 매핑 생성 실패. 프로젝트: {}, 에러: {}", projectKey, e.getMessage());
            throw new BusinessExceptionHandler("매핑 생성 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MappingResponseDto generateMappingForSingleScenario(ScenarioCUResponseDto result) {
        try {
            // 1. 개별 시나리오 매핑 데이터 준비
            MappingRequestDto requestDto = prepareSingleMappingData(result);
            
            // 2. AI 서비스 호출
            MappingResponseDto response = scenarioGenClient.MappingResponse(requestDto);
            
            log.info("개별 시나리오 매핑 생성 완료");                             
            return response;
            
        } catch (Exception e) {
            log.error("개별 시나리오 매핑 생성 실패. 시나리오 ID: {}, 에러: {}", result.getId(), e.getMessage());
            throw new BusinessExceptionHandler("매핑 생성 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
