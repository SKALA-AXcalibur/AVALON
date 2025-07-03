package com.sk.skala.axcalibur.feature.scenario.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.axcalibur.feature.scenario.client.ScenarioGenClient;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ApiMappingRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.ScenarioFlowRequestDto;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiFlowItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ApiMappingItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioFlowItem;
import com.sk.skala.axcalibur.feature.scenario.dto.request.item.ScenarioMappingItem;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ApiMappingResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.ScenarioFlowResponseDto;
import com.sk.skala.axcalibur.feature.scenario.dto.response.item.ApiMappingResponseItem;
import com.sk.skala.axcalibur.global.code.ErrorCode;
import com.sk.skala.axcalibur.global.entity.ApiListEntity;
import com.sk.skala.axcalibur.global.entity.MappingEntity;
import com.sk.skala.axcalibur.global.entity.ScenarioEntity;
import com.sk.skala.axcalibur.global.exception.BusinessExceptionHandler;
import com.sk.skala.axcalibur.global.repository.ApiListRepository;
import com.sk.skala.axcalibur.global.repository.MappingRepository;
import com.sk.skala.axcalibur.global.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시나리오 매핑 및 흐름도 생성 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioMappingServiceImpl implements ScenarioMappingService {

    private final ScenarioGenClient scenarioGenClient;
    private final ScenarioRepository scenarioRepository;
    private final ApiListRepository apiListRepository;
    private final MappingRepository mappingRepository;

    @Override
    @Transactional
    public ApiMappingResponseDto generateAndSaveMapping(String scenarioId) {
        log.info("=== 매핑 생성 시작 - 시나리오 ID: {} ===", scenarioId);
        
        // 1. 특정 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
            .orElseThrow(() -> new BusinessExceptionHandler("시나리오를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));
        
        // 2. 해당 프로젝트의 전체 API 정보 수집 (매핑 생성용)
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey_Id(scenario.getProject().getId());
        
        if (apiEntities.isEmpty()) {
            throw new BusinessExceptionHandler("매핑 생성을 위한 API 정보가 없습니다.", ErrorCode.NOT_VALID_ERROR);
        }
        
        // 3. 매핑 요청 DTO 생성
        ApiMappingRequestDto requestDto = createMappingRequest(List.of(scenario), apiEntities);
                requestDto.getScenarioList().size(), requestDto.getApiList().size());
        
        // 4. FastAPI 매핑 호출
        try {
            log.info("FastAPI 매핑 호출 시작...");
            ApiMappingResponseDto response = scenarioGenClient.sendMappingRequest(requestDto);
            
            // 5. 매핑 데이터 DB 저장
            saveMappingDataToDB(response, apiEntities);
            
            return response;
        } catch (Exception e) {
            throw new BusinessExceptionHandler("매핑 생성에 실패했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ScenarioFlowResponseDto generateFlowchart(String scenarioId) {
        log.info("=== 흐름도 생성 시작 - 시나리오 ID: {} ===", scenarioId);
        
        // 1. 특정 시나리오 조회
        ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
            .orElseThrow(() -> new BusinessExceptionHandler("시나리오를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_ERROR));
        
        // 2. 시나리오에 연결된 API 정보 수집 (매핑 테이블 이용)
        List<ApiListEntity> apiEntities = getLinkedApisFromMapping(scenario);
        log.info("매핑된 API 정보 조회 완료");
        
        // 만약 연결된 API가 없다면 프로젝트 전체 API로 흐름도 생성
        if (apiEntities.isEmpty()) {
            apiEntities = apiListRepository.findByProjectKey_Id(scenario.getProject().getId());
            
            if (apiEntities.isEmpty()) {
                throw new BusinessExceptionHandler("흐름도 생성을 위한 API 정보가 없습니다.", ErrorCode.NOT_VALID_ERROR);
            }
        }
        
        // 3. 흐름도 요청 DTO 생성
        ScenarioFlowRequestDto requestDto = createFlowchartRequest(List.of(scenario), apiEntities);
        log.info("흐름도 요청 DTO 생성 완료 - 시나리오 개수: {}", requestDto.getScenarioList().size());
        
        // 4. FastAPI 흐름도 호출
        try {
            ScenarioFlowResponseDto response = scenarioGenClient.sendFlowchartRequest(requestDto);
            return response;
        } catch (Exception e) {
            throw new BusinessExceptionHandler("흐름도 생성에 실패했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ApiMappingResponseDto generateAndSaveMappingForScenarios(List<ScenarioEntity> scenarios) {
        if (scenarios.isEmpty()) {
            throw new BusinessExceptionHandler("시나리오 목록이 비어있습니다.", ErrorCode.NOT_VALID_ERROR);
        }
        
        // 1. 프로젝트 ID 추출 (모든 시나리오가 같은 프로젝트라고 가정)
        Integer projectId = scenarios.get(0).getProject().getId();
        
        // 2. 해당 프로젝트의 API 정보 수집
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey_Id(projectId);
        
        // 3. 매핑 요청 DTO 생성
        ApiMappingRequestDto requestDto = createMappingRequest(scenarios, apiEntities);
        
        // 4. FastAPI 매핑 호출
        try {
          
            ApiMappingResponseDto response = scenarioGenClient.sendMappingRequest(requestDto);
            log.info("매핑 생성 완료");
            
            // 5. 매핑 데이터 DB 저장
            saveMappingDataToDB(response, apiEntities);
            log.info("=== 매핑 DB 저장 로직 완료 ===");
            
            return response;
        } catch (Exception e) {
            log.error("시나리오 매핑 생성 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler("매핑 생성에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ScenarioFlowResponseDto generateFlowchartForScenarios(List<ScenarioEntity> scenarios) {
        if (scenarios.isEmpty()) {
            throw new BusinessExceptionHandler("시나리오 목록이 비어있습니다.", ErrorCode.NOT_VALID_ERROR);
        }
        
        // 1. 프로젝트 ID 추출 (모든 시나리오가 같은 프로젝트라고 가정)
        Integer projectId = scenarios.get(0).getProject().getId();
        
        // 2. 해당 프로젝트의 API 정보 수집
        List<ApiListEntity> apiEntities = apiListRepository.findByProjectKey_Id(projectId);
        
        // 3. 흐름도 요청 DTO 생성
        ScenarioFlowRequestDto requestDto = createFlowchartRequest(scenarios, apiEntities);
        
        // 4. FastAPI 흐름도 호출
        try {
            ScenarioFlowResponseDto response = scenarioGenClient.sendFlowchartRequest(requestDto);
            log.info("시나리오 {} 개에 대한 흐름도 생성 완료", scenarios.size());
            return response;
        } catch (Exception e) {
            log.error("시나리오 흐름도 생성 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler("흐름도 생성에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 매핑 데이터를 DB에 저장
     */
    private void saveMappingDataToDB(ApiMappingResponseDto response, List<ApiListEntity> apiEntities) {
        try {
            
            if (response.getApiMapping() == null || response.getApiMapping().isEmpty()) {
                return;
            }
            
            log.info("수신된 매핑 데이터 개수: {}", response.getApiMapping().size());
            
            int savedCount = 0;
            Map<String, Integer> scenarioStepMap = new HashMap<>();
            
            for (ApiMappingResponseItem mappingItem : response.getApiMapping()) {
                String scenarioId = mappingItem.getScenarioId();
                String apiName = mappingItem.getApiName();
                
                
                // 시나리오 엔티티 조회
                ScenarioEntity scenario = scenarioRepository.findByScenarioId(scenarioId)
                    .orElse(null);
                    
                if (scenario == null) {
                    continue;
                }
                
                // 시나리오별 step 관리
                int step = scenarioStepMap.getOrDefault(scenarioId, 0) + 1;
                scenarioStepMap.put(scenarioId, step);
                
                // 첫 번째 API일 때만 기존 매핑 삭제 (중복 방지)
                if (step == 1) {
                    mappingRepository.deleteByScenarioKey_Id(scenario.getId());
                }
                
                
                // API 엔티티 조회 (이름으로 매칭)
                Optional<ApiListEntity> apiEntity = apiEntities.stream()
                    .filter(api -> api.getName().equals(apiName))
                    .findFirst();
                
                if (apiEntity.isPresent()) {
                    // 매핑 엔티티 생성 및 저장
                    MappingEntity mappingEntity = MappingEntity.builder()
                        .mappingId("map-" + scenario.getId() + "-" + step)
                        .step(step)
                        .scenarioKey(scenario)
                        .apiListKey(apiEntity.get())
                        .build();
                    
                    mappingRepository.save(mappingEntity);
                    savedCount++;

                } else {
                    apiEntities.forEach(api -> log.warn("  - {}", api.getName()));
                }
            }
            
        } catch (Exception e) {
            log.error("매핑 데이터 DB 저장 실패");
            // 매핑 저장 실패 시에도 전체 프로세스는 계속 진행
        }
    }

    /**
     * 매핑 요청 DTO 생성
     */
    private ApiMappingRequestDto createMappingRequest(List<ScenarioEntity> scenarios, List<ApiListEntity> apiEntities) {
        // 시나리오 정보를 매핑용 DTO로 변환
        List<ScenarioMappingItem> scenarioItems = scenarios.stream()
            .map(scenario -> ScenarioMappingItem.builder()
                .scenarioId(scenario.getScenarioId())
                .title(scenario.getName())
                .description(scenario.getDescription())
                .validation(scenario.getValidation())
                .build())
            .collect(Collectors.toList());

        // API 정보를 매핑용 DTO로 변환
        List<ApiMappingItem> apiItems = apiEntities.stream()
            .map(api -> ApiMappingItem.builder()
                .apiName(api.getName())
                .url(api.getPath())
                .method(api.getMethod())
                .description(api.getDescription())
                .parameters("") 
                .responseStructure("") 
                .build())
            .collect(Collectors.toList());

        return ApiMappingRequestDto.builder()
            .scenarioList(scenarioItems)
            .apiList(apiItems)
            .build();
    }

    /**
     * 흐름도 요청 DTO 생성
     */
    private ScenarioFlowRequestDto createFlowchartRequest(List<ScenarioEntity> scenarios, List<ApiListEntity> apiEntities) {
        // 시나리오 정보를 흐름도용 DTO로 변환
        List<ScenarioFlowItem> scenarioItems = scenarios.stream()
            .map(scenario -> {
                // 해당 시나리오와 연결된 API 목록 (매핑 테이블 기반)
                List<ApiFlowItem> apiFlowItems = getLinkedApisForScenario(scenario, apiEntities);

                return ScenarioFlowItem.builder()
                    .id(scenario.getScenarioId())
                    .description(scenario.getDescription())
                    .apiList(apiFlowItems)
                    .build();
            })
            .collect(Collectors.toList());

        return ScenarioFlowRequestDto.builder()
            .scenarioList(scenarioItems)
            .build();
    }
    
    /**
     * 매핑 테이블을 통해 시나리오에 연결된 API들을 조회
     */
    private List<ApiListEntity> getLinkedApisFromMapping(ScenarioEntity scenario) {
        // 매핑 테이블에서 해당 시나리오에 연결된 매핑 정보 조회
        List<MappingEntity> mappings = mappingRepository.findByScenarioKey_Id(scenario.getId());
        
        // 매핑된 API들 추출
        return mappings.stream()
            .map(MappingEntity::getApiListKey)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 시나리오와 연결된 API 목록 조회 (매핑 테이블 기반)
     */
    private List<ApiFlowItem> getLinkedApisForScenario(ScenarioEntity scenario, List<ApiListEntity> allApiEntities) {
        try {
            // 1. 매핑 테이블에서 해당 시나리오와 연결된 API들 조회
            List<MappingEntity> mappings = mappingRepository.findByScenarioKey_Id(scenario.getId());
            
            if (!mappings.isEmpty()) {
                
                // 매핑된 API들을 ApiFlowItem으로 변환
                return mappings.stream()
                    .map(mapping -> {
                        ApiListEntity api = mapping.getApiListKey();
                        return ApiFlowItem.builder()
                            .id(api.getApiListId())
                            .name(api.getName())
                            .description(api.getDescription())
                            .build();
                    })
                    .collect(Collectors.toList());
            } else {
                log.warn("시나리오 '{}' - 매핑 데이터 없음");
                
                // 2. 매핑이 없으면 프로젝트의 모든 API 사용 (fallback)
                return allApiEntities.stream()
                    .map(api -> ApiFlowItem.builder()
                        .id(api.getApiListId())
                        .name(api.getName())
                        .description(api.getDescription())
                        .build())
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("매핑 조회 실패");
            
            // 3. 예외 발생 시에도 전체 API 사용 (fallback)
            return allApiEntities.stream()
                .map(api -> ApiFlowItem.builder()
                    .id(api.getApiListId())
                    .name(api.getName())
                    .description(api.getDescription())
                    .build())
                .collect(Collectors.toList());
        }
    }
} 